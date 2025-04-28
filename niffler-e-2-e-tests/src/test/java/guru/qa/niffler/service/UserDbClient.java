package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.UserDaoSpringJdbc;
import guru.qa.niffler.data.entity.user.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;
import guru.qa.niffler.model.users.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static guru.qa.niffler.data.DataBases.dataSource;


public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    public UserJson createUserSpringJdbc(UserJson user) {

        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(ENCODER.encode(user.password()));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);


        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl())).create(authUserEntity);

        AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityRoles.values()).map(
                e -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setRole(e);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);


        new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl())).create(userAuthorities);

        return UserJson.fromEntity(
                new UserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl())).create(
                        UserEntity.fromJson(user)
                ));
    }

    //Закомментировано, поскольку в уроке 5.1 произошёл переход на Spring Jdbc
    //  и метод AuthAuthorityDao.create стал принимать AuthAuthorityEntity...

//    @SuppressWarnings("unchecked")
//    public UserJson createUser(UserJson userJson) {
//        XaFunction<UserJson> xaAuthF = new XaFunction<>(connection -> {
//            UserEntity ue = new UdUserDAOJdbc(connection).create(UserEntity.fromJson(userJson));
//            return UserJson.fromEntity(ue);
//        },
//                CFG.userdataJdbcUrl());
//
//        XaFunction<UserJson> xaUserDataF = new XaFunction<>(connection -> {
//            AuthUserEntity authUser = new AuthUserDaoJdbc(connection).create(AuthUserEntity.fromJson(userJson));
//            AuthAuthorityEntity authority = new AuthAuthorityEntity();
//
//            authority.setUserId(authUser.getId());
//            authority.setRole(AuthorityRoles.write);
//            new AuthAuthorityDaoJdbc(connection).create(authority);
//            authority.setRole(AuthorityRoles.read);
//            new AuthAuthorityDaoJdbc(connection).create(authority);
//            return UserJson.fromAuthorityEntity(authority);
//        },
//                CFG.authJdbcUrl());
//        return xaTransaction(TransactionIsolation.READ_COMMITTED, xaAuthF, xaUserDataF);
//    }
}