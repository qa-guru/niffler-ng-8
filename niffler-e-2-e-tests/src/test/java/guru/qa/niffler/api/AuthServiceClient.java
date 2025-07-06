package guru.qa.niffler.api;

import guru.qa.niffler.api.core.TradeSafeCookieStore;
import guru.qa.niffler.api.model.OAuthTokenResponse;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.*;

public interface AuthServiceClient {


    @GET("login")
    TestResponse<Void, Void> loginGet();

    @FormUrlEncoded
    @POST("login")
    TestResponse<Void, Void> loginPost(@Field("_csrf") String csrf,
                                       @Field("username") String username,
                                       @Field("password") String password);

    default TestResponse<Void, Void> loginPost(String username,
                                               String password) {
        return loginPost(TradeSafeCookieStore.INSTANCE.xsrfValue(), username, password);
    }

    @GET("register")
    TestResponse<Void, Void> registerGet();

    @FormUrlEncoded
    @POST("register")
    TestResponse<Void, Void> registerPost(@Field("_csrf") String csrf,
                                          @Field("username") String username,
                                          @Field("password") String password,
                                          @Field("passwordSubmit") String passwordSubmit);

    @GET("oauth2/authorize")
    TestResponse<Void, Void> authorizeGet(@Query("response_type") String responseType,
                                          @Query("client_id") String clientId,
                                          @Query("scope") String scope,
                                          @Query(value = "redirect_uri") String redirectUri,
                                          @Query("code_challenge") String codeChallenge,
                                          @Query("code_challenge_method") String codeChallengeMethod
    );

    default TestResponse<Void, Void> authorizeGet(String codeChallenge) {
        String redirectUri = Config.getInstance().frontUrl() + "authorized";
        return authorizeGet(
            "code",
            "client",
            "openid",
            redirectUri,
            codeChallenge,
            "S256"
        );
    }

    @FormUrlEncoded
    @POST("oauth2/token")
    TestResponse<OAuthTokenResponse, Void> tokenPost(@Field("code") String code,
                                                     @Field("redirect_uri") String redirectUri,
                                                     @Field("code_verifier") String codeVerifier,
                                                     @Field("grant_type") String grantType,
                                                     @Field(value = "client_id") String client);

    default TestResponse<OAuthTokenResponse, Void> tokenPost(String code, String codeVerifier) {
        String redirectUri = Config.getInstance().frontUrl() + "authorized";
        return tokenPost(
            code,
            redirectUri,
            codeVerifier,
            "authorization_code",
            "client"
        );
    }
}
