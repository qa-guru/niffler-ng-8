package guru.qa.niffler.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.SuccessRequestExecutor;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Response;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.Objects;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();
    private final AuthApi authApi;
    private static final String authorizedUri = CFG.frontUrl() +"authorized";
    private static final String CLIENT_ID = "client";


    public AuthApiClient() {
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    public void preRequest(String codeVerifier){
        sre.executeRequest(authApi.authorize(
                "code",
                CLIENT_ID,
                "openid",
                authorizedUri,
                generateCodeChallenge(codeVerifier),
                "S256"
        ));
    }

    public void login(UserJson user){
        TestMethodContextExtension.context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .put("code",loginAndGetCode(user));
    }

    private String loginAndGetCode(UserJson user){
        final Response<Void> response;

        try {
            response = authApi.login(
                    user.username(),
                    user.testData().password(),
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            )
                    .execute();
            assertTrue(response.isSuccessful());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.raw().request().url().queryParameter("code");

    }

    @Nonnull
    public String token(String code, String codeVerifier){
        JsonNode node = sre.executeRequest(authApi.token(
                CLIENT_ID,
                authorizedUri,
                "authorization_code",
                code,
                codeVerifier
        ));

        return Objects.requireNonNull(node)
                .get("id_token")
                .asText();
    }

    @Nonnull
    public String token(UserJson userJson){
        String codeVerifier = generateCodeVerifier();
        preRequest(codeVerifier);
        String code = loginAndGetCode(userJson);
        return token(code,codeVerifier);
    }
}
