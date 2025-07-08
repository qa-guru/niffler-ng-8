package guru.qa.niffler.service.impl.api;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.AuthEndpointClient;
import guru.qa.niffler.api.UserdataEndpointClient;
import guru.qa.niffler.api.core.TradeSafeCookieStore;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ParametersAreNonnullByDefault
public class UserApiClient extends AbstractApiClient implements UserClient {

    private final AuthEndpointClient authClient = ApiClients.authClient();
    private final UserdataEndpointClient userdataClient = ApiClients.userdataClient();

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
        List<UserParts> users = validateSuccessAndMapList(response, UserParts::of);
        if (users.size() > 1) {
            throw new IllegalStateException("Найдено больше пользователей, чем ожидалось: \n" + users);
        }
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    @SneakyThrows
    @Step("Создание пользователя")
    @Override
    public @Nonnull UserParts createUser(UserParts userPart) {
        TestResponse<Void, Void> registerGetResp = authClient.registerGet();
        validate(registerGetResp);
        String token = TradeSafeCookieStore.INSTANCE.xsrfValue();
        String username = userPart.getUsername();
        String password = userPart.getPassword();
        TestResponse<Void, Void> registerPostResp = authClient.registerPost(token, username, password, password);
        validate(registerPostResp);

        Stopwatch sw = Stopwatch.createStarted();
        long maxWaitTime = 5000;

        TestResponse<UserdataUserJson, ErrorJson> userCurrentGetResp = null;
        while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
            userCurrentGetResp = userdataClient.userCurrentGet(username);

            UserdataUserJson userdataBody = userCurrentGetResp.getBody();
            if (userCurrentGetResp.isSuccessful() && userdataBody != null && userdataBody.getId() != null) {
                validate(registerPostResp);
                return userPart.setUserdataUser(userdataBody);
            } else {
                Thread.sleep(100);
            }
        }
        return throwIllegalStateException(userCurrentGetResp);
    }

    @Step("Обновление пользователя")
    @Override
    public @Nonnull UserParts updateUser(UserParts userPart) {
        TestResponse<UserdataUserJson, ErrorJson> response = userdataClient.userUpdatePost(userPart.getUserdataUserJson());
        UserdataUserJson userdataUserJson = validateSuccessAndGetBody(response);
        return userPart.setUserdataUser(userdataUserJson);
    }

    @Step("Получение всех пользователей")
    @Override
    public List<UserParts> findAll() {
        TestResponse<List<UserdataUserJson>, ErrorJson> response = userdataClient.userAllGet("", null);
        return validateSuccessAndMapList(response, UserParts::of);
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

    @Step("Получение дружеских связей для пользователя")
    public List<UserdataUserJson> getAllFriends(String username) {
        TestResponse<List<UserdataUserJson>, ErrorJson> response = userdataClient.friendAllGet(username, null);
        return validateSuccessAndGetBody(response);
    }

    @Step("Удаление сгенерированных пользователей")
    @Override
    public void deleteAllGenUser() {
        throw new UnsupportedOperationException("Метод не реализован");
    }
}
