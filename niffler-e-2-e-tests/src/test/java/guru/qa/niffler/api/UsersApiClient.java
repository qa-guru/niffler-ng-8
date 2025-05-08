package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UsersApi usersApi = retrofit.create(UsersApi.class);

    @Override
    public UserJson createUser(String username, String password) {
        try {
            UserJson newUser = new UserJson(
                    null,
                    username,
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null,
                    new TestData(
                            password,
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    )
            );

            Response<UserJson> response = usersApi.addUser(newUser).execute();
            assertEquals(201, response.code(), "User creation failed");
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public Optional<UserJson> findUserByUsername(String username) {
        try {
            Response<List<UserJson>> response = usersApi.allUsers().execute();
            assertEquals(200, response.code(), "Failed to fetch all users");

            return response.body().stream()
                    .filter(u -> username.equals(u.username()))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find user by username", e);
        }
    }

    @Override
    public void createFriends(UserJson user, int count) {
        try {
            Response<List<UserJson>> response = usersApi.allUsers().execute();
            assertEquals(200, response.code(), "Failed to fetch all users");

            List<UserJson> candidates = response.body().stream()
                    .filter(u -> !u.id().equals(user.id()))
                    .limit(count)
                    .toList();

            for (UserJson friend : candidates) {
                Response<Void> addFriendResponse = usersApi.addFriend(user.id(), friend.id()).execute();
                assertEquals(200, addFriendResponse.code(), "Failed to add friend: " + friend.username());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create friends", e);
        }
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        try {
            Response<List<UserJson>> response = usersApi.allUsers().execute();
            assertEquals(200, response.code(), "Failed to fetch all users");

            List<UserJson> candidates = response.body().stream()
                    .filter(u -> !u.id().equals(targetUser.id()))
                    .limit(count)
                    .toList();

            for (UserJson inviter : candidates) {
                Response<Void> sendInvitationResponse = usersApi.sendInvitation(inviter.id(), targetUser.id()).execute();
                assertEquals(200, sendInvitationResponse.code(), "Failed to send invitation from: " + inviter.username());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create income invitations", e);
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson user, int count) {
        try {
            Response<List<UserJson>> response = usersApi.allUsers().execute();
            assertEquals(200, response.code(), "Failed to fetch all users");

            List<UserJson> candidates = response.body().stream()
                    .filter(u -> !u.id().equals(user.id()))
                    .limit(count)
                    .toList();

            for (UserJson invitee : candidates) {
                Response<Void> sendInvitationResponse = usersApi.sendInvitation(user.id(), invitee.id()).execute();
                assertEquals(200, sendInvitationResponse.code(), "Failed to send invitation to: " + invitee.username());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create outcome invitations", e);
        }
    }

    @Override
    public List<UserJson> incomeInvitations(String username) {
        throw new UnsupportedOperationException("incomeInvitations API is not supported yet");
    }

    @Override
    public List<UserJson> outcomeInvitations(String username) {
        throw new UnsupportedOperationException("outcomeInvitations API is not supported yet");
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        throw new UnsupportedOperationException("Removing friends is not supported by the API");
    }
}
