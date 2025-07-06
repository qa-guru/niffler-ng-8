package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.AuthEndpointClient;
import guru.qa.niffler.api.core.TradeSafeCookieStore;
import guru.qa.niffler.api.model.OAuthTokenResponse;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.util.OAuthUtils;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.retrofit.TestResponse;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;

public class AuthApiClient extends AbstractApiClient {

    public final AuthEndpointClient authEndpointClient = ApiClients.authClient();

    private AuthApiClient() {
    }

    public static AuthApiClient client() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final AuthApiClient INSTANCE = new AuthApiClient();
    }


    private TestResponse<Void, Void> loginPost(String username,
                                              String password) {
        return authEndpointClient.loginPost(TradeSafeCookieStore.INSTANCE.xsrfValue(), username, password);
    }

    private TestResponse<Void, Void> authorizeGet(String codeChallenge) {
        String redirectUri = Config.getInstance().frontUrl() + "authorized";
        return authEndpointClient.authorizeGet(
            "code",
            "client",
            "openid",
            redirectUri,
            codeChallenge,
            "S256"
        );
    }

    private TestResponse<OAuthTokenResponse, Void> tokenPost(String code, String codeVerifier) {
        String redirectUri = Config.getInstance().frontUrl() + "authorized";
        return authEndpointClient.tokenPost(
            code,
            redirectUri,
            codeVerifier,
            "authorization_code",
            "client"
        );
    }

    @Step("Логин пользователя через OAuth2 и получение токенов")
    public @Nonnull OAuthTokenResponse successLogin(@Nonnull UserParts user) {
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        TestResponse<Void, Void> authResponse = authorizeGet(codeChallenge);
        Assertions.assertTrue(authResponse.isSuccessful());

        TestResponse<Void, Void> loginResponse = loginPost(user.getUsername(), user.getPassword());
        Assertions.assertTrue(loginResponse.isSuccessful());

        TestResponse<OAuthTokenResponse, Void> tokenResponse = tokenPost(ApiLoginExtension.getCode(), codeVerifier);
        Assertions.assertTrue(tokenResponse.isSuccessful());

        OAuthTokenResponse oauthBody = tokenResponse.getBody();
        Assertions.assertNotNull(oauthBody);
        Assertions.assertNotNull(oauthBody.accessToken());
        Assertions.assertNotNull(oauthBody.idToken());
        return oauthBody;
    }

    public String successLoginAdnGetToken(UserParts user) {
        return successLogin(user).accessToken();
    }
}
