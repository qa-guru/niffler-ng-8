package guru.qa.niffler.test.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.AuthServiceClient;
import guru.qa.niffler.api.model.OAuthTokenResponse;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.util.OAuthUtils;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.retrofit.TestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthTest {

    private static final AuthServiceClient authClient = ApiClients.authClient();

    @Test
    @User
    public void test(UserParts user) {
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        TestResponse<Void, Void> authResponse = authClient.authorizeGet(codeChallenge);
        Assertions.assertTrue(authResponse.isSuccessful());

        TestResponse<Void, Void> loginResponse = authClient.loginPost(user.getUsername(), user.getPassword());
        Assertions.assertTrue(loginResponse.isSuccessful());

        String code = loginResponse.getOkhttpRawResponse().request().url().queryParameter("code");

        TestResponse<OAuthTokenResponse, Void> tokenResponse = authClient.tokenPost(code, codeVerifier);
        Assertions.assertTrue(tokenResponse.isSuccessful());

        OAuthTokenResponse oauthBody = tokenResponse.getBody();
        Assertions.assertNotNull(oauthBody);
        Assertions.assertNotNull(oauthBody.accessToken());
        Assertions.assertNotNull(oauthBody.idToken());
    }
}
