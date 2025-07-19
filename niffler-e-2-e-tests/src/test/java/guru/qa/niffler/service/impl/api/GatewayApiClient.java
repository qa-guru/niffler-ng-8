package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.GatewayEndpointClient;
import guru.qa.niffler.api.model.FriendJson;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.retrofit.TestResponse;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class GatewayApiClient extends AbstractApiClient {

    private final GatewayEndpointClient gatewayClient = ApiClients.gatewayClient();

    private GatewayApiClient() {
    }

    public static GatewayApiClient client() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final GatewayApiClient INSTANCE = new GatewayApiClient();
    }

    @Step("GET api/friends/all")
    private @Nonnull List<UserdataUserJson> allFriends(@Nonnull String bearerToken, @Nullable String searchQuery) {
        TestResponse<List<UserdataUserJson>, Void> response = gatewayClient.allFriends(bearerToken, searchQuery);
        return validateSuccessAndGetBody(response);
    }

    public @Nullable List<UserdataUserJson> allFriends(@Nullable String searchQuery) {
        return allFriends(getBearerToken(), searchQuery);
    }

    @Step("DELETE api/friends/remove")
    public void removeFriend(@Nonnull String targetUsername) {
        TestResponse<Void, Void> response = gatewayClient.removeFriend(getBearerToken(), targetUsername);
        validateSuccess(response);
    }

    @Step("POST api/invitations/accept")
    public @Nonnull UserdataUserJson acceptInvitation(String username) {
        TestResponse<UserdataUserJson, Void> response = gatewayClient.acceptInvitation(getBearerToken(), new FriendJson(username));
        return validateSuccessAndGetBody(response);
    }

    @Step("POST api/invitations/decline")
    public @Nonnull UserdataUserJson declineInvitation(String username) {
        TestResponse<UserdataUserJson, Void> response = gatewayClient.declineInvitation(getBearerToken(), new FriendJson(username));
        return validateSuccessAndGetBody(response);
    }

    @Step("POST api/invitations/send")
    public @Nonnull UserdataUserJson sendInvitation(String username) {
        TestResponse<UserdataUserJson, Void> response = gatewayClient.sendInvitation(getBearerToken(), new FriendJson(username));
        return validateSuccessAndGetBody(response);
    }

    private @Nonnull String getBearerToken() {
        return "Bearer " + ApiLoginExtension.getToken();
    }
}
