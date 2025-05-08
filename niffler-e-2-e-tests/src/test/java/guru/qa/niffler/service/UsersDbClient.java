package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc()
                .create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc()
                .create(authorityEntities);

        return UserJson.fromEntity(
                new UdUserDaoSpringJdbc()
                        .create(
                                UserEntity.fromJson(user)
                        ),
                null
        );
    }

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepository.create(userEntity(username)),
                            null
                    );
                }
        );
    }

    @Override
    public void createFriends(UserJson user, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(user.id())
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String friendUsername = randomUsername();
                    AuthUserEntity authUser = authUserEntity(friendUsername, "12345");
                    authUserRepository.create(authUser);

                    UserEntity friendEntity = userdataUserRepository.create(userEntity(friendUsername));

                    userdataUserRepository.addFriend(targetEntity, friendEntity);
                    userdataUserRepository.addFriend(friendEntity, targetEntity);

                    return null;
                });
            }
        }
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addIncomeInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public List<UserJson> incomeInvitations(String username) {
        return List.of();
    }


    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addOutcomeInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public Optional<UserJson> findUserByUsername(String username) {
        return xaTransactionTemplate.execute(() -> {
            Optional<AuthUserEntity> authUserOpt = authUserRepository.findByUsername(username);
            Optional<UserEntity> userdataUserOpt = userdataUserRepository.findByUsername(username);

            if (authUserOpt.isPresent() && userdataUserOpt.isPresent()) {
                UserEntity userdataUser = userdataUserOpt.get();
                return Optional.of(UserJson.fromEntity(userdataUser, null));
            }

            return Optional.empty();
        });
    }

    @Override
    public List<UserJson> outcomeInvitations(String username) {
        return List.of();
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {

    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}
