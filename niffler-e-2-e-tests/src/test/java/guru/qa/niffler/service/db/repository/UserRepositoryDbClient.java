package guru.qa.niffler.service.db.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.db.UsersDbClient;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public abstract class UserRepositoryDbClient extends UsersDbClient {

    protected final AuthUserRepository authUserRepository;

    protected final UserdataUserRepository userdataUserRepository;
    protected UserRepositoryDbClient(AuthUserRepository authUserRepository, UserdataUserRepository userdataUserRepository) {
        this.authUserRepository = authUserRepository;
        this.userdataUserRepository = userdataUserRepository;
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

    public void addFriend(UserJson targetUser, int count) {
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
                            userdataUserRepository.addInvitation(targetEntity, adressee, FriendshipStatus.ACCEPTED);
                            return null;
                        }
                );
            }
        }
    }

    public void addOutcomeInvitation(UserJson targetUser, int count) {
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
                            userdataUserRepository.addInvitation(targetEntity, adressee, FriendshipStatus.PENDING);
                            return null;
                        }
                );
            }
        }
    }

    public void addIncomeInvitation(UserJson targetUser, int count) {
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
                            userdataUserRepository.addInvitation(adressee, targetEntity, FriendshipStatus.PENDING);
                            return null;
                        }
                );
            }
        }
    }
}
