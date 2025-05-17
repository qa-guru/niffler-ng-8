package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.UserdataServiceClient;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.retrofit.TestResponse;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.util.RandomDataUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserApiClient implements UserClient {

    public final AuthServiceClient authClient = ApiClients.authClient();
    public final UserdataServiceClient userdataClient = ApiClients.userdataClient();

    private UserApiClient() {
    }

    public static UserApiClient client() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final UserApiClient INSTANCE = new UserApiClient();
    }

    @Override
    public Optional<UserParts> findByAuthId(UUID id) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @Override
    public Optional<UserParts> findByUsername(String username) {
        TestResponse<List<UserdataUserJson>, Void> response = userdataClient.userAllGet(null, username);
        List<UserParts> users = validateAndMapList(response);
        if (users.size() > 1) {
            throw new IllegalStateException("Найдено больше пользователей, чем ожидалось: \n" + users);
        }
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    @Override
    public UserParts createUser(UserParts userPart) {
        TestResponse<Void, Void> registerGetResp = authClient.registerGet();
        validate(registerGetResp);
        String token = registerGetResp.getHeaders().get("X-XSRF-TOKEN");
        String username = userPart.getUsername();
        String password = userPart.getPassword();
        TestResponse<Void, Void> registerPostResp = authClient.registerPost(token, username, password, password);
        validate(registerPostResp);
        TestResponse<UserdataUserJson, Void> userCurrentGetResp = userdataClient.userCurrentGet(username);
        validate(registerPostResp);
        return userPart.setUserdataUser(userCurrentGetResp.getBody());
    }

    @Override
    public UserParts updateUser(UserParts userPart) {
        TestResponse<UserdataUserJson, Void> response = userdataClient.userUpdatePost(userPart.getUserdataUserJson());
        return extractResp(response, resp -> userPart.setUserdataUser(resp.getBody()));
    }

    @Override
    public List<UserParts> findAll() {
        TestResponse<List<UserdataUserJson>, Void> response = userdataClient.userAllGet(null, null);
        return extractResp(response,
            resp -> resp.getBody().stream()
                .map(bodyElement -> UserParts.of(bodyElement))
                .collect(Collectors.toList())
        );
    }

    @Override
    public void deleteUser(UserParts userPart) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @Override
    public void createIncomeInvitation(UserParts targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserParts user = RandomDataUtils.genDefaultUser();
            user = createUser(user);
            TestResponse<UserdataUserJson, Void> response =
                userdataClient.invitationSendPost(user.getUsername(), targetUser.getUsername());
            validate(response);
            targetUser.getTestData().getInInviteNames().add(user.getUsername());
        }
    }

    @Override
    public void createOutcomeInvitation(UserParts targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserParts user = RandomDataUtils.genDefaultUser();
            user = createUser(user);
            TestResponse<UserdataUserJson, Void> response =
                userdataClient.invitationSendPost(targetUser.getUsername(), user.getUsername());
            validate(response);
            targetUser.getTestData().getOutInviteNames().add(user.getUsername());
        }
    }

    @Override
    public void createFriends(UserParts targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserParts user = RandomDataUtils.genDefaultUser();
            user = createUser(user);
            TestResponse<UserdataUserJson, Void> sendResponse =
                userdataClient.invitationSendPost(targetUser.getUsername(), user.getUsername());
            validate(sendResponse);
            TestResponse<UserdataUserJson, Void> acceptResponse =
                userdataClient.invitationAcceptPost(user.getUsername(), targetUser.getUsername());
            validate(acceptResponse);
            targetUser.getTestData().getFriendsNames().add(user.getUsername());
        }
    }

    @Override
    public void deleteAllGenUser() {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    private <REQ, RESP> void validate(TestResponse<REQ, RESP> response) {
        if (!response.isSuccessful()) {
            throw new IllegalStateException("Запрос выполнился некорректно: \n" + response.getRetrofitRawResponse());
        }
    }

    private <REQ, RESP, R> R extractResp(TestResponse<REQ, RESP> response,
                                         Function<TestResponse<REQ, RESP>, R> extractor) {
        if (response.isSuccessful()) {
            return extractor.apply(response);
        } else {
            throw new IllegalStateException("Запрос выполнился некорректно: \n" + response.getRetrofitRawResponse());
        }
    }

    private List<UserParts> validateAndMapList(TestResponse<List<UserdataUserJson>, Void> response) {
        return extractResp(response,
            resp -> resp.getBody().stream()
                .map(bodyElement -> UserParts.of(bodyElement))
                .collect(Collectors.toList())
        );
    }

}
