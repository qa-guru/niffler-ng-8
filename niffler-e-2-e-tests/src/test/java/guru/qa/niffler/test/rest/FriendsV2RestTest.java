package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.api.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsV2RestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

    @User(friends = 1, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final Page<UserJson> responseBody = gatewayApiClient.allFriends("Bearer " + bearerToken, 0,10,null);
        assertEquals(3, responseBody.getContent().size());
    }

    @ApiLogin
    @User(friends = 2, incomeInvitations = 2)
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedWithFiltrationBySearchQuery(UserJson user,
                                                                                @Token String bearerToken) {
        final UserJson testFriend = user.testData().friends().getFirst();

        final RestResponsePage<UserJson> response = gatewayApiClient.allFriends(
                "Bearer " + bearerToken,
                0,
                10,
                testFriend.username());

        assertEquals(1, response.getContent().size());

        final UserJson foundedFriend = response.getContent().getFirst();

        assertEquals(FriendshipStatus.FRIEND, foundedFriend.friendshipStatus());
        assertEquals(testFriend.id(), foundedFriend.id());
        assertEquals(testFriend.username(), foundedFriend.username());
    }
}