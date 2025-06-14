package guru.qa.niffler.api;

import guru.qa.niffler.api.util.SessionCookieJar;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.retrofit.TestResponseAdapterFactory;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiClients {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .cookieJar(new SessionCookieJar())
        .addNetworkInterceptor(
            new AllureOkHttp3()
                .setRequestTemplate("http-request-custom.ftl")
                .setResponseTemplate("http-response-custom.ftl")
        )
        .build();

    private static final Config CFG = Config.getInstance();

    private ApiClients() {
    }

    public static SpendServiceClient spendClient() {
        return SpendHolder.INSTANCE;
    }

    public static UserdataServiceClient userdataClient() {
        return UserdataHolder.INSTANCE;
    }

    public static GhServiceClient ghClient() {
        return GhHolder.INSTANCE;
    }

    public static AuthServiceClient authClient() {
        return AuthHolder.INSTANCE;
    }

    private static class SpendHolder {
        private static final SpendServiceClient INSTANCE = buildClient(CFG.spendUrl(), SpendServiceClient.class);
    }

    private static class UserdataHolder {
        private static final UserdataServiceClient INSTANCE = buildClient(CFG.userdataUrl(), UserdataServiceClient.class);
    }

    private static class GhHolder {
        private static final GhServiceClient INSTANCE = buildClient(CFG.ghUrl(), GhServiceClient.class);
    }

    private static class AuthHolder {
        private static final AuthServiceClient INSTANCE = buildClient(CFG.authUrl(), AuthServiceClient.class);
    }

    private static <T> T buildClient(String baseUrl, Class<T> apiClass) {
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(TestResponseAdapterFactory.create())
            .build()
            .create(apiClass);
    }

}
