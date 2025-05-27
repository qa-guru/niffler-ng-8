package guru.qa.niffler.service.api;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.utils.SuccessRequestExecutor;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import retrofit2.Call;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String DEFAULT_PASSWORD = "12345";

    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();

    private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataApi userdataApi = new RestClient.EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);


    @Override
    @Step("Create user with username {username} and password {password}")
    public UserJson createUser(String username, String password) {
        return sre.<UserJson>executeRequest(
                authApi.getRegisterPage(),
                authApi.register(
                        username,
                        password,
                        password,
                        null
                ),
                userdataApi.currentUser(username)
        ).withPassword(DEFAULT_PASSWORD);
    }

    @Override
    @Step("Create {count} income invitations for user {targetUser}")
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if(count >0 ){
            List<Call<?>> calls = new ArrayList<>();
            for(int i = 0; i < count; i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username, DEFAULT_PASSWORD);
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
                UserJson friend = createUser(username, DEFAULT_PASSWORD);
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
                UserJson friend = createUser(username, DEFAULT_PASSWORD);
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
}
