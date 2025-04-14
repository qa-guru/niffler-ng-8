package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.UserdataUserDaoJdbc;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.util.List;
import java.util.function.Supplier;

public class UserDbService extends AbstractDbClient {

    private final String AUTH_DB_URL = CFG.authJdbcUrl();
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc(AUTH_DB_URL);
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc(AUTH_DB_URL);

    private final String USERDATA_DB_URL = CFG.userdataJdbcUrl();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc(USERDATA_DB_URL);

    public UserParts createUser(UserParts userJson) {
        return xaTransaction(() -> {
            AuthUserEntity authUser = AuthUserEntity.fromJson(userJson.authUserJson());
            UserdataUserEntity userdataUser = UserdataUserEntity.fromJson(userJson.userdataUserJson());

            authUser = authUserDao.createAuthUser(authUser);
            for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
                authority.setUser(authUser);
                AuthAuthorityEntity authAuthority = authAuthorityDao.createAuthAuthority(authority);
                authUser.addAuthorities(authAuthority);
            }
            userdataUser = userdataUserDao.createUserdata(userdataUser);
            UserdataUserJson userdataUserJson = UserdataUserJson.fromEntity(userdataUser);
            AuthUserJson authUserJson = AuthUserJson.fromEntity(authUser);
            return new UserParts(authUserJson, userdataUserJson);
        });
    }

    public void deleteUser(UserParts userJson) {
        xaTransaction(() -> {
            deleteAuthUserAndAuthority(userJson.authUserJson());
            deleteUserdataUser(userJson.userdataUserJson());
        });
    }

    private void deleteUserdataUser(UserdataUserJson userJson) {
        UserdataUserEntity userdataUser;
        if (userJson.getId() != null) {
            userdataUser = UserdataUserEntity.fromJson(userJson);
        } else if (userJson.getUsername() != null) {
            userdataUser = userdataUserDao.findUserdataByUsername(userJson.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                    ));
        } else {
            throw new IllegalArgumentException("id и username == null");
        }
        userdataUserDao.deleteUserdata(userdataUser);
    }

    private void deleteAuthUserAndAuthority(AuthUserJson userJson) {
        AuthUserEntity authUser;
        if (userJson.getId() != null) {
            authUser = AuthUserEntity.fromJson(userJson);
        } else if (userJson.getUsername() != null) {
            authUser = authUserDao.findAuthUserByUsername(userJson.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                    ));
            List<AuthAuthorityEntity> authorities = authAuthorityDao.findAuthAuthorityByUserId(authUser.getId());
            authUser.addAuthorities(authorities);
        } else {
            throw new IllegalArgumentException("id и username == null");
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
