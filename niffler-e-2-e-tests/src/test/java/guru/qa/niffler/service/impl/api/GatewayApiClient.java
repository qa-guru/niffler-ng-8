package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.GatewayEndpointClient;
import guru.qa.niffler.api.model.UserdataUserJson;
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

    @Step("Логин пользователя через OAuth2 и получение токенов")
    public @Nonnull List<UserdataUserJson> allFriends(@Nonnull String bearerToken, @Nullable String searchQuery) {
        TestResponse<List<UserdataUserJson>, Void> response = gatewayClient.allFriends(bearerToken, searchQuery);
        return validateSuccessAndGetBody(response);
    }
}
