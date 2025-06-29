package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.UserdataServiceClient;
import guru.qa.niffler.api.model.ErrorJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.retrofit.TestResponse;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.util.RandomDataUtils;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
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

    @Step("Поиск пользователя по id")
    @Override
    public @Nonnull Optional<UserParts> findByAuthId(UUID id) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @Step("Поиск пользователя по имени")
    @Override
    public @Nonnull Optional<UserParts> findByUsername(String username) {
        TestResponse<List<UserdataUserJson>, ErrorJson> response = userdataClient.userAllGet("", username);
        List<UserParts> users = validateAndMapList(response);
        if (users.size() > 1) {
            throw new IllegalStateException("Найдено больше пользователей, чем ожидалось: \n" + users);
        }
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    @Step("Создание пользователя")
    @Override
    public @Nonnull UserParts createUser(UserParts userPart) {
        TestResponse<Void, Void> registerGetResp = authClient.registerGet();
        validate(registerGetResp);
        String token = registerGetResp.getHeaders().get("X-XSRF-TOKEN");
        String username = userPart.getUsername();
        String password = userPart.getPassword();
        TestResponse<Void, Void> registerPostResp = authClient.registerPost(token, username, password, password);
        validate(registerPostResp);
        TestResponse<UserdataUserJson, ErrorJson> userCurrentGetResp = userdataClient.userCurrentGet(username);
        validate(registerPostResp);
        return userPart.setUserdataUser(userCurrentGetResp.getBody());
    }

    @Step("Обновление пользователя")
    @Override
    public @Nonnull UserParts updateUser(UserParts userPart) {
        TestResponse<UserdataUserJson, ErrorJson> response = userdataClient.userUpdatePost(userPart.getUserdataUserJson());
        return extractResp(response, resp -> userPart.setUserdataUser(resp.getBody()));
    }

    @Step("Получение всех пользователей")
    @Override
    public List<UserParts> findAll() {
        TestResponse<List<UserdataUserJson>, ErrorJson> response = userdataClient.userAllGet("", null);
        return extractResp(response,
            resp -> resp.getBody().stream()
                .map(UserParts::of)
                .collect(Collectors.toList())
        );
    }

    @Step("Удаление пользователя")
    @Override
    public void deleteUser(UserParts userPart) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @Step("Создание входящих запросов на дружбу")
    @Override
    public void createIncomeInvitation(UserParts targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserParts user = RandomDataUtils.genDefaultUser();
            user = createUser(user);
            TestResponse<UserdataUserJson, ErrorJson> response =
                userdataClient.invitationSendPost(user.getUsername(), targetUser.getUsername());
            validate(response);
            targetUser.getTestData().getInInviteNames().add(user.getUsername());
        }
    }

    @Step("Создание исходящих запросов на дружбу")
    @Override
    public void createOutcomeInvitation(UserParts targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserParts user = RandomDataUtils.genDefaultUser();
            user = createUser(user);
            TestResponse<UserdataUserJson, ErrorJson> response =
                userdataClient.invitationSendPost(targetUser.getUsername(), user.getUsername());
            validate(response);
            targetUser.getTestData().getOutInviteNames().add(user.getUsername());
        }
    }

    @Step("Создание дружеских связей для пользователя")
    @Override
    public void createFriends(UserParts targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserParts user = RandomDataUtils.genDefaultUser();
            user = createUser(user);
            TestResponse<UserdataUserJson, ErrorJson> sendResponse =
                userdataClient.invitationSendPost(targetUser.getUsername(), user.getUsername());
            validate(sendResponse);
            TestResponse<UserdataUserJson, ErrorJson> acceptResponse =
                userdataClient.invitationAcceptPost(user.getUsername(), targetUser.getUsername());
            validate(acceptResponse);
            targetUser.getTestData().getFriendsNames().add(user.getUsername());
        }
    }

    @Step("Удаление сгенерированных пользователей")
    @Override
    public void deleteAllGenUser() {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    private <REQ, RESP> void validate(TestResponse<REQ, RESP> response) {
        if (!response.isSuccessful()) {
            throwIllegalStateException(response);
        }
    }

    private <REQ, RESP, R> @Nonnull R extractResp(TestResponse<REQ, RESP> response,
                                         Function<TestResponse<REQ, RESP>, R> extractor) {
        if (response.isSuccessful()) {
            return extractor.apply(response);
        } else {
            return throwIllegalStateException(response);
        }
    }

    @SneakyThrows
    private <REQ, RESP, R> R throwIllegalStateException(TestResponse<REQ, RESP> response) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Запрос выполнился некорректно:");
        sj.add(response.getRetrofitRawResponse().toString());
        sj.add(response.getErrorBody().toString());
        throw new IllegalStateException(sj.toString());
    }

    private @Nonnull List<UserParts> validateAndMapList(TestResponse<List<UserdataUserJson>, ErrorJson> response) {
        return extractResp(response,
            resp -> resp.getBody().stream()
                .map(UserParts::of)
                .collect(Collectors.toList())
        );
    }

}
