package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.api.AuthApiClient;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTest {

    private final AuthApiClient authApi = new AuthApiClient();

    @User
    @Test
    void oauthTest(UserJson user){
        final String codeVerifier = generateCodeVerifier();

        authApi.preRequest(codeVerifier);
        authApi.login(user);

        String token = authApi.token(ApiLoginExtension.getCode(),codeVerifier);
        assertNotNull(token);
        System.out.println(token);
    }

    @User
    @Test
    void oauthTestMethod(UserJson user){
        String token = authApi.token(user);
        assertNotNull(token);
        System.out.println(token);
    }

    @Test
    @User
    @ApiLogin
    void oauthTestMethod(@Token String token, UserJson user){
        assertNotNull(token);
        System.out.println(token);
        System.out.println(user);
    }

    @Test
    @ApiLogin(username = "Timofey",password = "12345")
    void oauthTestMethodTwo(@Token String token, UserJson user){
        assertNotNull(token);
        System.out.println(token);
        System.out.println(user);
    }
}
