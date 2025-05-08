package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersClient {
    UserJson createUser(String username, String password);

    void createFriends(UserJson user, int count);

    void createIncomeInvitations(UserJson requester, int count);

    List<UserJson> incomeInvitations(String username);

    void createOutcomeInvitations(UserJson addressee, int count);

    Optional<UserJson> findUserByUsername(String username);

    List<UserJson> outcomeInvitations(String username);

    void removeFriend(UUID userId, UUID friendId);
}
