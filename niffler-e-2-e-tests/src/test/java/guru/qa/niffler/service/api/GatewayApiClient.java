package guru.qa.niffler.service.api;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.SuccessRequestExecutor;
import io.qameta.allure.Step;

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

    @Step("Remove friend using /api/friends/remove endpoint")
    public void removeFriend(String bearerToken, String username) {
         sre.executeRequest(gatewayApi.removeFriend(
                bearerToken,
                username
        ));
    }

    @Step("Accept invitation using /api/invitations/accept endpoint")
    @Nonnull
    public UserJson acceptInvitation(String bearerToken, String friendUsername) {
        return requireNonNull(sre.executeRequest(gatewayApi.acceptInvitation(
                bearerToken,
                new FriendJson(friendUsername)
        )));
    }

    @Step("Decline invitation using /api/invitations/decline endpoint")
    @Nonnull
    public UserJson declineInvitation(String bearerToken, String friendUsername) {
        return requireNonNull(sre.executeRequest(gatewayApi.declineInvitation(
                bearerToken,
                new FriendJson(friendUsername)
        )));
    }

    @Step("Send invitation using /api/invitations/send endpoint")
    @Nonnull
    public UserJson sendInvitation(String bearerToken, String friendUsername) {
        return requireNonNull(sre.executeRequest(gatewayApi.sendInvitation(
                bearerToken,
                new FriendJson(friendUsername)
        )));
    }

    @Step("Get all people using /api/users/all endpoint")
    @Nonnull
    public List<UserJson> allPeople(String bearerToken, @Nullable String searchQuery) {
        return requireNonNull(sre.executeRequest(gatewayApi.allUsers(
                bearerToken,
                searchQuery
        )));
    }
}