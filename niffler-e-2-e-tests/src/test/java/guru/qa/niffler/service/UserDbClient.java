package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOJdbc;
import guru.qa.niffler.data.entity.user.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;
import guru.qa.niffler.model.TransactionIsolation;
import guru.qa.niffler.model.users.UserJson;

import static guru.qa.niffler.data.DataBases.xaTransaction;


public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    @SuppressWarnings("unchecked")
    public UserJson createUser(UserJson userJson) {
        XaFunction<UserJson> xaAuthF = new XaFunction<>(connection -> {
            UserEntity ue = new UserdataUserDAOJdbc(connection).create(UserEntity.fromJson(userJson));
            return UserJson.fromEntity(ue);
        },
                CFG.userdataJdbcUrl());

        XaFunction<UserJson> xaUserDataF = new XaFunction<>(connection -> {
            AuthUserEntity authUser = new AuthUserDaoJdbc(connection).create(AuthUserEntity.fromJson(userJson));
            AuthAuthorityEntity authority = new AuthAuthorityEntity();

            authority.setUserId(authUser.getId());
            authority.setRole(AuthorityRoles.write);
            new AuthAuthorityDaoJdbc(connection).create(authority);
            authority.setRole(AuthorityRoles.read);
            new AuthAuthorityDaoJdbc(connection).create(authority);
            return UserJson.fromAuthorityEntity(authority);
        },
                CFG.authJdbcUrl());
        return xaTransaction(TransactionIsolation.READ_COMMITTED, xaAuthF, xaUserDataF);
    }
}