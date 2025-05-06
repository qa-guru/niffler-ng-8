package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.Optional;

public interface UsersClient {
    UserJson createUser(String username, String password);

    void createFriends(UserJson user, int count);

    void createIncomeInvitations(UserJson requester, int count);

    void createOutcomeInvitations(UserJson addressee, int count);

    Optional<UserJson> findUserByUsername(String username);
}
