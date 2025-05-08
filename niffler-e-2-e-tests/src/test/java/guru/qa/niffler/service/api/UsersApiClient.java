package guru.qa.niffler.service.api;

import guru.qa.niffler.utils.SuccessRequestExecutor;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofitAuth = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final Retrofit retrofitUd = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthApi authApi = retrofitAuth.create(AuthApi.class);
    private final UserdataApi userdataApi = retrofitUd.create(UserdataApi.class);
    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();


    @Override
    public @Nonnull UserJson createUser(String username, String password) {
        sre.executeRequest(
                authApi.getRegisterPage()
        );
        sre.executeRequest(authApi.register(
                username,
                password,
                password,
                null
        ));

        return sre.executeRequest(
                userdataApi.currentUser(username)
        )
                .withPassword(defaultPassword);
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if(count >0 ){
            for(int i = 0; i < count; i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username,defaultPassword);
                sre.executeRequest(
                        userdataApi.sendInvitation(
                                friend
                                        .username(),
                                targetUser.username()
                ));
            }
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if(count>0){
            for(int i = 0; i<count;i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username,defaultPassword);
                sre.executeRequest(
                        userdataApi.sendInvitation(
                                targetUser
                                        .username(),
                                friend.username()
                        )
                );
            }
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        if(count>0){
            for(int i = 0; i<count;i++){
                final String username = RandomDataUtils.randomUsername();
                UserJson friend = createUser(username,defaultPassword);
                sre.executeRequest(
                        userdataApi.sendInvitation(
                                friend
                                        .username(),
                                targetUser.username()
                        )
                );
                sre.executeRequest(
                        userdataApi.acceptInvitation(
                                friend.username(),
                                targetUser.username()
                        )
                );
            }
        }
    }
}
