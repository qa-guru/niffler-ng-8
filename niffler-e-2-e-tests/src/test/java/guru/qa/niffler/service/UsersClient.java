package guru.qa.niffler.service;

import guru.qa.niffler.api.UserApiClient;
import guru.qa.niffler.model.users.UserJson;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UsersClient {

    static UsersClient getInstance() {
        return new UserApiClient();
    }

    UserJson createUserTxJdbc(UserJson user);

    UserJson createUserTxChainedJdbc(UserJson user);

    UserJson createUser(UserJson user);

    List<UserJson> allUsers(String username, String searchQuery);

    void addIncomeInvitation(UUID requesterUUID, UUID addresseeUUID);

    void addIncomeInvitation(UserJson requester, UserJson addressee);

    void addOutcomeInvitation(UUID requesterUUID, UUID addresseeUUID);

    void addFriend(UUID requesterUUID, UUID addresseeUUID);

    void deleteUser(UserJson user);

    UUID getUserId(UserJson user);

    void clearPhotoDataByUsername(String username);
}
