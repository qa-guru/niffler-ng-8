package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );
    private final AuthUserRepository authUserRepository;
    private final UserdataUserRepository userdataUserRepository;

    public UsersDbClient(Realization type){
        this.authUserRepository = type.getAuthUserRepository();
        this.userdataUserRepository = type.getUdUserRepository();
    }

    public UsersDbClient(){
        this.authUserRepository = Realization.HIBERNATE.getAuthUserRepository();
        this.userdataUserRepository = Realization.HIBERNATE.getUdUserRepository();
    }

    @Override
    @Step("Create user with username {username} and password {password}")
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(username,password);
            authUserRepository.create(authUserEntity);
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username))
            )
                    .withPassword(password);
        });
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

    protected UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    @Override
    @Step("Create {count} friend for user {targetUser}")
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            xaTransactionTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                AuthUserEntity authUser = authUserEntity(username, "12345");
                authUserRepository.create(authUser);
                UserEntity adressee = userdataUserRepository.create(userEntity(username));
                userdataUserRepository.addInvitation(targetEntity, adressee, FriendshipStatus.ACCEPTED);
                targetUser.testData().friends().add(
                        UserJson.fromEntity(adressee).withPassword("12345")
                );
            }
            return null;
            });

        }
    }

    @Override
    @Step("Create {count} outcome invitations for user {targetUser}")
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            xaTransactionTemplate.execute(() -> {
                for (int i = 0; i < count; i++) {

                    String username = randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity adressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addInvitation(targetEntity, adressee, FriendshipStatus.PENDING);
                    targetUser.testData().outcomeInvitations().add(
                            UserJson.fromEntity(adressee).withPassword("12345")
                    );
                }
                return null;
            });

        }
    }

    @Override
    @Step("Create {count} income invitations for user {targetUser}")
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            xaTransactionTemplate.execute(() -> {
                for (int i = 0; i < count; i++) {
                    String username = randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity adressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addInvitation(adressee, targetEntity, FriendshipStatus.PENDING);
                    targetUser.testData().incomeInvitations().add(
                            UserJson.fromEntity(adressee).withPassword("12345")
                    );
                }
                return null;
            });
        }
    }

}