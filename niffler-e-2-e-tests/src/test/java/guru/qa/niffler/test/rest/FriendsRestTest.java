package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.RestTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.service.impl.api.GatewayApiClient;
import guru.qa.niffler.service.impl.db.UserDbClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.api.model.UserdataUserJson.FriendshipStatus.FRIEND;
import static guru.qa.niffler.api.model.UserdataUserJson.FriendshipStatus.INVITE_SENT;
import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;

@RestTest
public class FriendsRestTest {

    @RegisterExtension
    private static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLOGinExtension();
    private static final GatewayApiClient gatewayClient = GatewayApiClient.client();
    public static final UserDbClient userDbClient = new UserDbClient();

    @User(withFriend = 1, withInInvite = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway() {
        List<UserdataUserJson> friends = gatewayClient.allFriends(null);
        Assertions.assertEquals(3, friends.size());
    }

    @User(withFriend = 2, withInInvite = 3)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeFiltered(UserParts user) {
        String friendName = user.getTestData().getFriendsNames().getFirst();
        List<UserdataUserJson> friends = gatewayClient.allFriends(friendName);
        Assertions.assertEquals(1, friends.size());
    }

    @User(withFriend = 1)
    @ApiLogin
    @Test
    void friendshipMustBeDeleted(UserParts user) {
        String friendName = user.getTestData().getFriendsNames().getFirst();
        gatewayClient.removeFriend(friendName);
        List<UserdataFriendshipEntity> friendships = userDbClient.selectFriendshipsByRequesterId(user.getUserdataId());
        Assertions.assertTrue(friendships.isEmpty());
    }

    @User(withInInvite = 1)
    @ApiLogin
    @Test
    void friendRequestMustBeAccepted(UserParts user) {
        String futureFriendName = user.getTestData().getInInviteNames().getFirst();
        UserdataUserJson responseBody = gatewayClient.acceptInvitation(futureFriendName);

        Assertions.assertEquals(futureFriendName, responseBody.getUsername());
        Assertions.assertEquals(FRIEND, responseBody.getFriendshipStatus());

        List<UserdataFriendshipEntity> friendships = userDbClient.selectFriendshipsByAddresseeId(user.getUserdataId());
        Assertions.assertEquals(1, friendships.size());
        UserdataFriendshipEntity friendship = friendships.getFirst();
        Assertions.assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
        Assertions.assertNotNull(friendship.getAddressee());
    }

    @User(withInInvite = 1)
    @ApiLogin
    @Test
    void friendRequestMustBeDeclined(UserParts user) {
        String futureFriendName = user.getTestData().getInInviteNames().getFirst();
        UserdataUserJson responseBody = gatewayClient.declineInvitation(futureFriendName);

        Assertions.assertEquals(futureFriendName, responseBody.getUsername());

        List<UserdataFriendshipEntity> friendships = userDbClient.selectFriendshipsByAddresseeId(user.getUserdataId());
        Assertions.assertTrue(friendships.isEmpty());
    }

    @User
    @ApiLogin
    @Test
    void afterSendInvitationMustBeOutRequest(UserParts user) {
        UserParts futureFriend = userDbClient.createUser(genDefaultUser());
        String futureFriendName = futureFriend.getUsername();
        UserdataUserJson responseBody = gatewayClient.sendInvitation(futureFriendName);

        Assertions.assertEquals(futureFriendName, responseBody.getUsername());
        Assertions.assertEquals(INVITE_SENT, responseBody.getFriendshipStatus());

        List<UserdataFriendshipEntity> friendships = userDbClient.selectFriendshipsByRequesterId(user.getUserdataId());
        Assertions.assertEquals(1, friendships.size());
        UserdataFriendshipEntity friendship = friendships.getFirst();
        Assertions.assertEquals(FriendshipStatus.PENDING, friendship.getStatus());
        Assertions.assertNotNull(friendship.getAddressee());
        Assertions.assertEquals(futureFriend.getUserdataId(), friendship.getAddressee().getId());
    }
}
