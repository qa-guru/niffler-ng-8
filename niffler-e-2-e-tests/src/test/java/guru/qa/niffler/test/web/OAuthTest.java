package guru.qa.niffler.test.web;

import guru.qa.niffler.api.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest extends BaseUITest {

    String actualLogin = CFG.mainUserLogin();
    String actualPass = CFG.mainUserPass();


    @Test
    void oauthTest() {
        AuthApiClient authApiClient = new AuthApiClient();
        String token = authApiClient.loginAs(actualLogin, actualPass);

        Assertions.assertNotNull(token);
    }
}
