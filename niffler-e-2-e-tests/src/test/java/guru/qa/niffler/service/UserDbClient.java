package guru.qa.niffler.service;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.UserDaoSpringJdbc;
import guru.qa.niffler.data.dao.interfaces.AuthAuthorityDao;
import guru.qa.niffler.data.dao.interfaces.AuthUserDao;
import guru.qa.niffler.data.dao.interfaces.UserDao;
import guru.qa.niffler.data.entity.user.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.TransactionIsolation;
import guru.qa.niffler.model.users.UserJson;
import org.springframework.dao.DataAccessException;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;


public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UserDao userDao = new UserDaoSpringJdbc();

    private final TransactionTemplate transactionTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
    );

    private final TransactionTemplate xaTransactionTemplateChained = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            DataSources.dataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            DataSources.dataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

    public UserJson createUserTxJdbc(UserJson user) {
        return xaTransactionTemplate.execute(TransactionIsolation.READ_UNCOMMITTED, () -> {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setUsername(user.username());
                    authUserEntity.setPassword(ENCODER.encode(user.password()));
                    authUserEntity.setEnabled(true);
                    authUserEntity.setAccountNonExpired(true);
                    authUserEntity.setAccountNonLocked(true);
                    authUserEntity.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.create(authUserEntity);

                    AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityRoles.values()).map(
                            e -> {
                                AuthAuthorityEntity ae = new AuthAuthorityEntity();
                                ae.setUserId(createdAuthUser.getId());
                                ae.setRole(e);
                                return ae;
                            }
                    ).toArray(AuthAuthorityEntity[]::new);


                    authAuthorityDao.create(userAuthorities);
                    return UserJson.fromEntity(
                            userDao.create(
                                    UserEntity.fromJson(user))

                    );
                }
        );
    }

    public UserJson createUserTxChainedJdbc(UserJson user) {
        return xaTransactionTemplateChained.execute(status -> {
            try {
                AuthUserEntity authUserEntity = new AuthUserEntity();
                authUserEntity.setUsername(user.username());
                authUserEntity.setPassword(ENCODER.encode(user.password()));
                authUserEntity.setEnabled(true);
                authUserEntity.setAccountNonExpired(true);
                authUserEntity.setAccountNonLocked(true);
                authUserEntity.setCredentialsNonExpired(true);

                AuthUserEntity createdAuthUser = authUserDao.create(authUserEntity);

                AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityRoles.values()).map(
                        e -> {
                            AuthAuthorityEntity ae = new AuthAuthorityEntity();
                            ae.setUserId(createdAuthUser.getId());
                            ae.setRole(e);
                            return ae;
                        }
                ).toArray(AuthAuthorityEntity[]::new);


                authAuthorityDao.create(userAuthorities);
                return UserJson.fromEntity(
                        userDao.create(
                                UserEntity.fromJson(user))

                );
            } catch (DataAccessException e) {
                status.setRollbackOnly();
                throw new RuntimeException("Failed to create user", e);
            }
        });
    }

    public UserJson createUserJdbc(UserJson user) {

        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(ENCODER.encode(user.password()));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);


        AuthUserEntity createdAuthUser = authUserDao.create(authUserEntity);

        AuthAuthorityEntity[] userAuthorities = Arrays.stream(AuthorityRoles.values()).map(
                e -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setRole(e);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);


        authAuthorityDao.create(userAuthorities);

        return UserJson.fromEntity(
                userDao.create(UserEntity.fromJson(user)));
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