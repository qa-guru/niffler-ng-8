package guru.qa.niffler.service.api;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.model.*;
import guru.qa.niffler.utils.SuccessRequestExecutor;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();

    private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataApi userdataApi = new RestClient.EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);


    @Override
    @Step("Create user with username {username} and password {password}")
    @Nonnull
    public UserJson createUser(String username, String password) {
        return Objects.requireNonNull(
                sre.<UserJson>executeRequest(
                    authApi.getRegisterPage(),
                    authApi.register(
                            username,
                            password,
                            password,
                            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                    ),
                    userdataApi.currentUser(username)
                ))
                .withPassword(CFG.defaultPassword());
    }

    @Override
    @Step("Create {count} income invitations for user {targetUser}")
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if(count >0 ){
            List<Call<?>> calls = new ArrayList<>();
            for(int i = 0; i < count; i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username, CFG.defaultPassword());
                calls.add(userdataApi.sendInvitation(
                        friend
                                .username(),
                        targetUser.username()
                ));
            }
            sre.executeRequest(calls.toArray(Call<?>[]::new));
        }
    }

    @Override
    @Step("Create {count} outcome invitations for user {targetUser}")
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if(count>0){
            List<Call<?>> calls = new ArrayList<>();
            for(int i = 0; i<count;i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username, CFG.defaultPassword());
                calls.add(
                        userdataApi.sendInvitation(
                                targetUser
                                        .username(),
                                friend.username()
                        )
                );
            }
            sre.executeRequest(calls.toArray(Call<?>[]::new));
        }
    }

    @Override
    @Step("Create {count} friend for user {targetUser}")
    public void createFriends(UserJson targetUser, int count) {
        if(count>0){
            List<Call<?>> calls = new ArrayList<>();
            for(int i = 0; i<count;i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username, CFG.defaultPassword());
                calls.add(
                        userdataApi.sendInvitation(
                                friend
                                        .username(),
                                targetUser.username()
                        )
                );
                calls.add(
                        userdataApi.acceptInvitation(
                                friend.username(),
                                targetUser.username()
                        )
                );
            }
            sre.executeRequest(calls.toArray(Call<?>[]::new));
        }
    }

    @Step("Get all users")
    public List<UserJson> allUsers(String username,@Nullable String query){
        return sre.executeRequest(userdataApi.allUsers(username,query));
    }

    @Nonnull
    @Step("get user's {user} friends")
    public List<UserJson> getFriends(String username){
        return usersEnrichment(username, FriendshipStatus.FRIEND);
    }

    @Nonnull
    @Step("get user's {user} income invitations")
    public List<UserJson> getIncomeInvitations(String username){
        return usersEnrichment(username, FriendshipStatus.INVITE_RECEIVED);
    }

    @Nonnull
    @Step("get user's {user} outcome invitations")
    public List<UserJson> getOutcomeInvitations(String username){
        return usersEnrichment(username, FriendshipStatus.INVITE_SENT);
    }

    @Nonnull
    private List<UserJson> usersEnrichment(String username, FriendshipStatus friendshipStatus){
        Call<List<UserJson>> call = switch (friendshipStatus) {
            case INVITE_SENT ->
                    userdataApi.allUsers(username, null);
            case FRIEND, INVITE_RECEIVED ->
                    userdataApi.friends(username, null);
        };

        List<UserJson> users = Objects.requireNonNull(sre.executeRequest(call));

        if (CollectionUtils.isEmpty(users)) {
            return new ArrayList<>();
        }
        return users
                .stream()
                .filter(friend -> friendshipStatus.equals(friend.friendshipStatus()))
                .toList();
    }

    @Step("Get current user's profile data")
    public UserJson currentUser(String username){
        return sre.executeRequest(userdataApi.currentUser(username));
    }
}
