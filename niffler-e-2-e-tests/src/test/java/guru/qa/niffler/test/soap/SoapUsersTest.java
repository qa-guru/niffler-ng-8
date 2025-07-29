package guru.qa.niffler.test.soap;


import guru.qa.jaxb.userdata.CurrentUserRequest;
import guru.qa.jaxb.userdata.UserResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.soap.UserdataSoapClient;
import guru.qa.jaxb.userdata.UsersResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static guru.qa.jaxb.userdata.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SoapTest
public class SoapUsersTest {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @Test
    @User
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = userdataSoapClient.currentUser(request);
        assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }


    @Test
    @User(friends = 3)
    void getAllFriendsPageForUserTest(UserJson userJson) throws IOException {
        UsersResponse response = userdataSoapClient.allFriendsPage(userJson, 1, 10);
        assertEquals(1, response.getTotalPages().intValue());
        assertEquals(userJson.testData().friends().size(), response.getTotalElements().intValue());
    }

    @Test
    @User(friends = 2)
    void getAllFriendsForUserFilteringByUsernameTest(UserJson userJson) throws IOException {
        String username = userJson.testData().friends().getFirst().username();
        UsersResponse response = userdataSoapClient.allFriendsFilteringByUsername(userJson, username);
        assertEquals(1, response.getUser().size());
        assertTrue(userJson.testData().friends().stream()
                .map(UserJson::username)
                .toList().contains(response.getUser().getFirst().getUsername()));
    }

    @Test
    @User(friends = 1)
    void removeFriendTest(UserJson userJson) throws IOException {
        String friendToBeRemovedUsername = userJson.testData().friends().getFirst().username();
        userdataSoapClient.removeFriend(userJson, friendToBeRemovedUsername);
        UsersResponse response = userdataSoapClient.allFriendsFilteringByUsername(userJson, friendToBeRemovedUsername);
        assertEquals(0, response.getUser().size());
    }

    @Test
    @User(incomeInvitations = 1)
    void acceptIncomeInvitationTest(UserJson userJson) throws IOException {
        String friendToBeAddedUsername = userJson.testData().incomeInvitations().getFirst().username();
        userdataSoapClient.acceptIncomeInvitation(userJson, friendToBeAddedUsername);
        UsersResponse response = userdataSoapClient.allFriendsFilteringByUsername(userJson, friendToBeAddedUsername);
        assertEquals(1, response.getUser().size());
        assertEquals(friendToBeAddedUsername, response.getUser().getFirst().getUsername());
    }

    @Test
    @User(incomeInvitations = 1)
    void declineIncomeInvitationTest(UserJson userJson) throws IOException {
        String friendToBeDeclinedUsername = userJson.testData().incomeInvitations().getFirst().username();
        userdataSoapClient.declineIncomeInvitation(userJson, friendToBeDeclinedUsername);
        UsersResponse response = userdataSoapClient.allFriendsFilteringByUsername(userJson, friendToBeDeclinedUsername);
        assertEquals(0, response.getUser().size());
    }

    @Test
    @User
    void sendInvitationTest(UserJson userJson) throws IOException {
        String friendToBeRequestedUsername = "Ilya";
        UserResponse response = userdataSoapClient.sendInvitation(userJson, friendToBeRequestedUsername);
        assertEquals(INVITE_SENT, response.getUser().getFriendshipStatus());
    }
}