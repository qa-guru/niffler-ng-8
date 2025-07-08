package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.RestTest;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.service.impl.api.GatewayApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

@RestTest
public class FriendsRestTest {

    @RegisterExtension
    private static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLOGinExtension();
    private static final GatewayApiClient gatewayClient = GatewayApiClient.client();

    @User(withFriend = 1, withInInvite = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        List<UserdataUserJson> friends = gatewayClient.allFriends("Bearer " + bearerToken, null);
        Assertions.assertEquals(3, friends.size());
    }
}
