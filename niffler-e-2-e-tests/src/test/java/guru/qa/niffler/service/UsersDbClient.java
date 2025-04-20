package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Arrays;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserJson createUser(UserJson user) {
        return UserJson.fromEntity(
                xaTransaction(
                        Connection.TRANSACTION_READ_COMMITTED,
                        createAuthUserFunction(user),
                        createUserDataFunction(user)
                )
        );
    }

    private XaFunction<UserEntity> createAuthUserFunction(UserJson user) {
        return new XaFunction<>(
                connection -> {
                    AuthUserEntity authUser = createAuthUserEntity(user);
                    AuthUserEntity createdUser = new AuthUserDaoJdbc(connection).create(authUser);

                    Arrays.stream(Authority.values())
                            .map(authority -> createAuthorityEntity(createdUser.getId(), authority))
                            .forEach(ae -> new AuthAuthorityDaoJdbc(connection).create(ae));

                    return new UserEntity();
                },
                CFG.authJdbcUrl()
        );
    }

    private XaFunction<UserEntity> createUserDataFunction(UserJson user) {
        return new XaFunction<>(
                connection -> {
                    // Создаем пользователя в userdata БД
                    UserEntity userEntity = new UserEntity();
                    userEntity.setUsername(user.username());
                    userEntity.setFullname(user.fullname());
                    userEntity.setCurrency(user.currency());
                    return new UserdataUserDaoJdbc(connection).createUser(userEntity);
                },
                CFG.userdataJdbcUrl()
        );
    }

    private AuthUserEntity createAuthUserEntity(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode(randomPassword()));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        return authUser;
    }

    private AuthorityEntity createAuthorityEntity(UUID userId, Authority authority) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setUserId(userId);
        ae.setAuthority(authority);
        return ae;
    }
}