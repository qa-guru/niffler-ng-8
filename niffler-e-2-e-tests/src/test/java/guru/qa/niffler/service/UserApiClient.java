package guru.qa.niffler.service;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.interfaces.AuthApi;
import guru.qa.niffler.api.interfaces.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.users.UserJson;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class UserApiClient implements UsersClient {
    protected static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.ActionRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserApi userdataApi = new RestClient.ActionRestClient(CFG.userdataUrl()).create(UserApi.class);

    @Override
    public UserJson createUserTxJdbc(UserJson user) {
        return null;
    }

    @Override
    public UserJson createUserTxChainedJdbc(UserJson user) {
        return null;
    }


    @Override
    public UserJson createUser(UserJson userJson) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    userJson.username(),
                    userJson.password(),
                    userJson.password(),
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
            return requireNonNull(userdataApi.currentUser(userJson.username()).execute().body());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserJson> allUsers(String username, String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.allUsers(username, searchQuery).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(200, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }

    @Override
    public void addIncomeInvitation(UUID requesterUUID, UUID addresseeUUID) {

    }

    @Override
    public void addIncomeInvitation(UserJson requester, UserJson addressee) {

    }

    @Override
    public void addOutcomeInvitation(UUID requesterUUID, UUID addresseeUUID) {

    }

    @Override
    public void addFriend(UUID requesterUUID, UUID addresseeUUID) {

    }

    @Override
    public void deleteUser(UserJson user) {

    }

    @Override
    public UUID getUserId(UserJson user) {
        return null;
    }

    @Override
    public void clearPhotoDataByUsername(String username) {

    }
}
