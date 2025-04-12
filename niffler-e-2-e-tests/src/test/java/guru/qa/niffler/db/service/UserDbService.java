package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.UserJson;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.db.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.db.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.util.List;
import java.util.function.Supplier;

public class UserDbService extends AbstractDbClient {

    private final Config CFG = Config.getInstance();

    private final String AUTH_DB_URL = CFG.authJdbcUrl();
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc(AUTH_DB_URL);
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc(AUTH_DB_URL);

    private final String USERDATA_DB_URL = CFG.userdataJdbcUrl();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc(USERDATA_DB_URL);

    public UserJson createUser(UserJson userJson) {
        return xaTransaction(() -> {
            AuthUserEntity authUser = AuthUserEntity.fromJson(userJson);
            UserdataUserEntity userdataUser = UserdataUserEntity.fromJson(userJson);

            authUser = authUserDao.createAuthUser(authUser);
            for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
                authority.setUser(authUser);
                AuthAuthorityEntity authAuthority = authAuthorityDao.createAuthAuthority(authority);
                authUser.addAuthorities(authAuthority);
            }
            userdataUser = userdataUserDao.createUserdata(userdataUser);
            return UserJson.fromEntity(authUser, userdataUser);
        });
    }

    public void deleteUser(UserJson userJson) {
        xaTransaction(() -> {
            deleteAuthUserAndAuthority(userJson);
            deleteUserdataUser(userJson);
        });
    }

    private void deleteUserdataUser(UserJson userJson) {
        UserdataUserEntity userdataUser = UserdataUserEntity.fromJson(userJson);
        if (userdataUser.getId() == null) {
            userdataUser = userdataUserDao.findUserdataByUsername(userdataUser.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                    ));
        }
        userdataUserDao.deleteUserdata(userdataUser);
    }

    private void deleteAuthUserAndAuthority(UserJson userJson) {
        AuthUserEntity authUser = AuthUserEntity.fromJson(userJson);
        if (authUser.getId() == null) {
            authUser = authUserDao.findAuthUserByUsername(authUser.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                    ));
            List<AuthAuthorityEntity> authorities = authAuthorityDao.findAuthAuthorityByUserId(authUser.getId());
            authUser.addAuthorities(authorities);
        }
        for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
            authAuthorityDao.deleteAuthAuthority(authority);
        }
        authUserDao.deleteAuthUser(authUser);
    }

    public <T> T xaTransaction(Supplier<T> supplier) {
        return xaTransaction(supplier, AUTH_DB_URL, USERDATA_DB_URL);
    }

    public void xaTransaction(Runnable runnable) {
        xaTransaction(runnable, AUTH_DB_URL, USERDATA_DB_URL);
    }

}
