package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.UserJson;

import java.util.List;

public interface UsersClient {
    UserJson getUser(String username);

    UserJson createUser(String username, String password);

    void createIncomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson targetUser, int count);

    void createFriends(UserJson targetUser, int count);

    List<UserJson> allUsers(String username, String query);
}