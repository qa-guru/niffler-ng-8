package guru.qa.niffler.test.fake;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.TradeSafeCookieStore;
import guru.qa.niffler.api.model.OAuthTokenResponse;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.util.OAuthUtils;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.Token;
import guru.qa.niffler.retrofit.TestResponse;
import guru.qa.niffler.service.impl.api.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static guru.qa.niffler.api.core.TradeSafeCookieStore.JSESSIONID;

public class AuthTest {

    private static final AuthApiClient authClient = AuthApiClient.client();

    @Test
    @User
    public void test1(UserParts user) {
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

    @Test
    @User
    public void test2(UserParts user) {
        String token = authClient.successLoginAdnGetToken(user);
        Assertions.assertNotNull(token);

        Selenide.open(Config.getInstance().frontUrl());
        Selenide.localStorage().setItem("id_token", token);
        WebDriverRunner.getWebDriver().manage().addCookie(
            new Cookie(JSESSIONID, TradeSafeCookieStore.INSTANCE.jSessionIdValue())
        );
    }

    @Test
    @User
    @ApiLogin
    public void test3(@Token String token, UserParts user) {
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(user);
    }
}
