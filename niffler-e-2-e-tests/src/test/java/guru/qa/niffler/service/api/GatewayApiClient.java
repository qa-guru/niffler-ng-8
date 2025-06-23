package guru.qa.niffler.service.api;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.SuccessRequestExecutor;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Get all friends & income invitations using /api/friends/all endpoint")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken,
                                     @Nullable String searchQuery) {
        return requireNonNull(
                sre.executeRequest(gatewayApi.allFriends(bearerToken,searchQuery))
        );
    }
}