package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.interfaces.AuthApi;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.utils.OauthUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthApiClient extends RestClient {
    public static final String CODE = "code";
    public static final String CLIENT_ID = "client";
    public static final String OPEN_ID = "openid";
    public static final String CODE_CHALLENGE_METHOD = "S256";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String XSRF_TOKEN = "XSRF-TOKEN";
    public static final String ID_TOKEN = "id_token";
    public static final String REDIRECT_URI = CFG.frontUrl() + "authorized";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true);
        authApi = retrofit.create(AuthApi.class);
    }

    public String loginAs(String userName, String password) {

        final String codeVerifier = OauthUtils.generateCodeVerifier();
        final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

        preRequest(codeChallenge);
        String code = login(userName, password);
        return token(code, codeVerifier);
    }

    private void preRequest(String codeChallenge) {
        Response<Void> response;
        try {
            response = authApi.authorize(
                            CODE,
                            CLIENT_ID,
                            OPEN_ID,
                            REDIRECT_URI,
                            codeChallenge,
                            CODE_CHALLENGE_METHOD
                    ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
    }

    private String login(String userName, String password) {
        Response<Void> response;

        try {
            response = authApi.login(
                    ThreadSafeCookieStore.INSTANCE.cookieValue(XSRF_TOKEN),
                    userName,
                    password
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.code());
        return StringUtils.substringAfter(String.valueOf(response.raw().request().url()), "code=");
    }

    private String token(String code, String codeVerifier) {
        Response<JsonNode> response;
        try {
            response = authApi.token(
                    code,
                    REDIRECT_URI,
                    codeVerifier,
                    GRANT_TYPE,
                    CLIENT_ID
            ).execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return Objects.requireNonNull(response.body()).get(ID_TOKEN).asText();
    }
}