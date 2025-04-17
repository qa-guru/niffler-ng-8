package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;

import java.util.List;

public class UserDbService extends AbstractDbClient {

    private final String AUTH_DB_URL = CFG.authJdbcUrl();
    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc(AUTH_DB_URL);
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc(AUTH_DB_URL);

    private final String USERDATA_DB_URL = CFG.userdataJdbcUrl();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc(USERDATA_DB_URL);

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(AUTH_DB_URL, USERDATA_DB_URL);

    public UserParts createUser(UserParts userJson) {
        return xaTxTemplate.execute(() -> {
            UserdataUserEntity userdataUser = UserdataUserEntity.fromJson(userJson.userdataUserJson());
            AuthUserEntity authUser = AuthUserEntity.fromJson(userJson.authUserJson());
            authUser = authUserDao.create(authUser);
            for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
                authority.setUser(authUser);
                AuthAuthorityEntity authAuthority = authAuthorityDao.create(authority);
                authUser.addAuthorities(authAuthority);
            }
            userdataUser = userdataUserDao.create(userdataUser);
            UserdataUserJson userdataUserJson = UserdataUserJson.fromEntity(userdataUser);
            AuthUserJson authUserJson = AuthUserJson.fromEntity(authUser);
            return new UserParts(authUserJson, userdataUserJson);
        });
    }

    public void deleteUser(UserParts userJson) {
        xaTxTemplate.execute(() -> {
            deleteAuthUserAndAuthority(userJson.authUserJson());
            deleteUserdataUser(userJson.userdataUserJson());
        });
    }

    private void deleteUserdataUser(UserdataUserJson userJson) {
        UserdataUserEntity userdataUser;
        if (userJson.getId() != null) {
            userdataUser = UserdataUserEntity.fromJson(userJson);
        } else if (userJson.getUsername() != null) {
            userdataUser = userdataUserDao.findByUsername(userJson.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                    ));
        } else {
            throw new IllegalArgumentException("id и username == null");
        }
        userdataUserDao.delete(userdataUser);
    }

    private void deleteAuthUserAndAuthority(AuthUserJson userJson) {
        AuthUserEntity authUser;
        if (userJson.getId() != null) {
            authUser = AuthUserEntity.fromJson(userJson);
        } else if (userJson.getUsername() != null) {
            authUser = authUserDao.findByUsername(userJson.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                    ));
            List<AuthAuthorityEntity> authorities = authAuthorityDao.findByUserId(authUser.getId());
            authUser.addAuthorities(authorities);
        } else {
            throw new IllegalArgumentException("id и username == null");
        }
        for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
            authAuthorityDao.delete(authority);
        }
        authUserDao.delete(authUser);
    }

}
