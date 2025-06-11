package guru.qa.niffler.test.auth;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.AuthApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

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

        String code = (String) TestMethodContextExtension.context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .get("code");

        String token = authApi.token(code,codeVerifier);
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
}
