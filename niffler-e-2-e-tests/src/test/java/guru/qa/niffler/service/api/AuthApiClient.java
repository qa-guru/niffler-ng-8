package guru.qa.niffler.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.SuccessRequestExecutor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Objects;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final SuccessRequestExecutor sre = new SuccessRequestExecutor();
    private final AuthApi authApi;
    private static final String authorizedUri = CFG.frontUrl() +"authorized";
    private static final String CLIENT_ID = "client";


    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
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
        sre.executeRequest(
                authApi.login(
                        user.username(),
                        user.testData().password(),
                        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                )
        );
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
        login(userJson);
        return token(ApiLoginExtension.getCode(),codeVerifier);
    }
}
