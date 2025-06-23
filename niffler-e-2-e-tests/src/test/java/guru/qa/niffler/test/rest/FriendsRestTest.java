package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.api.GatewayApiClient;
import guru.qa.niffler.service.api.UsersApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.model.FriendshipStatus.FRIEND;
import static guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class FriendsRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();

    @User(friends = 1, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final List<UserJson> responseBody = gatewayApiClient.allFriends("Bearer " + bearerToken, null);
        assertEquals(3, responseBody.size());
    }

    @ApiLogin
    @User(friends = 2, incomeInvitations = 2)
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedWithFiltrationBySearchQuery(UserJson user,
                                                                                @Token String bearerToken) {
        final UserJson testFriend = user.testData().friends().getFirst();

        final List<UserJson> response = gatewayApiClient.allFriends(
                "Bearer " + bearerToken,
                testFriend.username());

        assertEquals(1, response.size());

        final UserJson foundedFriend = response.getFirst();

        assertEquals(FRIEND, foundedFriend.friendshipStatus());
        assertEquals(testFriend.id(), foundedFriend.id());
        assertEquals(testFriend.username(), foundedFriend.username());
    }

    @ApiLogin
    @User(friends = 1)
    @Test
    void friendshipShouldBeRemoved(UserJson user,
                                   @Token String bearerToken) {
        final String friendUsername = user.testData().friends().getFirst().username();
        gatewayApiClient.removeFriend("Bearer " + bearerToken, friendUsername);

        assertTrue(gatewayApiClient.allFriends("Bearer " + bearerToken, null).isEmpty());
    }

    @ApiLogin
    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationShouldBeAccepted(UserJson user,
                                          @Token String bearerToken) {
        final UserJson incomeInvitation = user.testData().incomeInvitations().getFirst();

        gatewayApiClient.acceptInvitation(
                "Bearer " + bearerToken,
                incomeInvitation.username()
        );

        final List<UserJson> friends = gatewayApiClient.allFriends(
                "Bearer " + bearerToken,
                null
        );

        final UserJson acceptedFriend = friends.getFirst();

        assertEquals(1, friends.size());
        assertEquals(incomeInvitation.username(), acceptedFriend.username());
        assertEquals(FRIEND, acceptedFriend.friendshipStatus());
    }

    @ApiLogin
    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationShouldBeDeclined(UserJson user,
                                          @Token String bearerToken) {
        final UserJson incomeInvitation = user.testData().incomeInvitations().getFirst();

        UserJson declinedFriend = gatewayApiClient.declineInvitation(
                "Bearer " + bearerToken,
                incomeInvitation.username()
        );

        final List<UserJson> friends = gatewayApiClient.allFriends(
                "Bearer " + bearerToken,
                null
        );

        final List<UserJson> incomeInvitations = friends
                .stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
                .toList();

        assertEquals(incomeInvitation.username(), declinedFriend.username());
        assertEquals(0, friends.size());
        assertEquals(0, incomeInvitations.size());
    }

    @ApiLogin
    @User
    @Test
    void shouldCreateIncomeAndOutcomeInvitationsAfterSendingFriendshipRequest(UserJson user,
                                                                              @Token String bearerToken) {
        final String friendUsername = randomUsername();

        final UserJson friendUser = usersApiClient.createUser(
                friendUsername,
                "12345"
        );

        gatewayApiClient.sendInvitation(
                "Bearer " + bearerToken,
                friendUsername
        );

        final UserJson actualOutcomeInvitation = gatewayApiClient.allPeople(
                "Bearer " + bearerToken,
                friendUser.username()
        ).getFirst();

        final UserJson actualIncomeInvitation = usersApiClient.getFriends(friendUsername).getFirst();

        assertEquals(friendUsername, actualOutcomeInvitation.username());
        assertEquals(user.username(), actualIncomeInvitation.username());
    }
}