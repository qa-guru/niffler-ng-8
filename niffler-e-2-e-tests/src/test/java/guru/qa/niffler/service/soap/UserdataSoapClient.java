package guru.qa.niffler.service.soap;


import guru.qa.niffler.model.rest.UserJson;
import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @NotNull
    @Step("Get current user info using SOAP")
    public UserResponse currentUser(CurrentUserRequest request) throws IOException {
        return userdataSoapApi.currentUser(request).execute().body();
    }

    @Step("Get friends info page for user using SOAP")
    public UsersResponse allFriendsPage(UserJson userJson, int page, int size) throws IOException {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(userJson.username());
        request.setPageInfo(pageInfo);
        return requireNonNull(userdataSoapApi.friendsPage(request).execute().body());
    }

    @NotNull
    @Step("Get friends info for user filtering by username using SOAP")
    public UsersResponse allFriendsFilteringByUsername(UserJson userJson, String username) throws IOException {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(userJson.username());
        request.setSearchQuery(username);
        return requireNonNull(userdataSoapApi.friends(request).execute().body());
    }

    @Step("Remove friend for user using SOAP")
    public void removeFriend(UserJson userJson, String friendToBeRemoved) throws IOException {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(userJson.username());
        request.setFriendToBeRemoved(friendToBeRemoved);
        userdataSoapApi.removeFriend(request).execute();
    }

    @Step("Accept income invitation for user using SOAP")
    public void acceptIncomeInvitation(UserJson userJson, String friendToBeAddedUsername) throws IOException {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(userJson.username());
        request.setFriendToBeAdded(friendToBeAddedUsername);
        userdataSoapApi.acceptInvitation(request).execute();
    }

    @Step("Decline income invitation for user using SOAP")
    public void declineIncomeInvitation(UserJson userJson, String friendToBeDeclinedUsername) throws IOException {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(userJson.username());
        request.setInvitationToBeDeclined(friendToBeDeclinedUsername);
        userdataSoapApi.declineInvitation(request).execute();
    }

    @NotNull
    @Step("Send invitation using SOAP")
    public UserResponse sendInvitation(UserJson userJson, String friendToBeRequestedUsername) throws IOException {
        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(userJson.username());
        request.setFriendToBeRequested(friendToBeRequestedUsername);
        return requireNonNull(userdataSoapApi.sendInvitation(request).execute().body());
    }
}
