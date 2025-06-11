package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new AllureOkHttp3()
                    .setRequestTemplate("req-attachment.ftl")
                    .setResponseTemplate("resp-attachment.ftl"))
            .build();

    private final Retrofit retrofitUser = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final Retrofit retrofitAuth = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();


    private final UserApi userApi = retrofitUser.create(UserApi.class);
    private static final String defaultPassword = "12345";


    @NotNull
    @Override
    public UserJson createUser(String username, String password) {
        return new UsersDbClient().createUser(username, password);
    }

    @Override
    public void addIncomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson addressee = createRandomUser();
                execute(userApi.sendInvitation(addressee.username(), targetUser.username()));
                targetUser.testData().incomeInvitations().add(addressee);

            }
        }
    }

    @Override
    public void addOutcomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson addressee = createRandomUser();
                execute(userApi.sendInvitation(targetUser.username(), addressee.username()));
                targetUser.testData().outcomeInvitations().add(addressee);
            }
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson addressee = createRandomUser();
                execute(userApi.sendInvitation(targetUser.username(), addressee.username()));
                execute(userApi.acceptInvitation(addressee.username(),targetUser.username()));
                targetUser.testData().friends().add(addressee);
            }
        }
    }

    public UserJson createRandomUser(){
        return createUser(RandomDataUtils.randomUsername(), defaultPassword);
    }

    @Nullable
    private <T> T execute(Call<T> call) {
        final Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertTrue(response.isSuccessful());
        return response.body();
    }
}
