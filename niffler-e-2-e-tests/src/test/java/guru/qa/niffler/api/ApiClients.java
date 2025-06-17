package guru.qa.niffler.api;

import guru.qa.niffler.api.util.SessionCookieJar;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.retrofit.TestResponseAdapterFactory;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiClients {

    private ApiClients() {
    }

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .cookieJar(new SessionCookieJar())
        .addNetworkInterceptor(
            new AllureOkHttp3()
                .setRequestTemplate("http-request-custom.ftl")
                .setResponseTemplate("http-response-custom.ftl")
        )
        .build();

    private static final Config CFG = Config.getInstance();
    private static final SpendServiceClient SPEND_CLIENT = buildClient(CFG.spendUrl(), SpendServiceClient.class);
    private static final UserdataServiceClient USERDATA_CLIENT = buildClient(CFG.userdataUrl(), UserdataServiceClient.class);
    private static final GhServiceClient GH_CLIENT = buildClient(CFG.ghUrl(), GhServiceClient.class);
    private static final AuthServiceClient AUTH_CLIENT = buildClient(CFG.authUrl(), AuthServiceClient.class);

    public static SpendServiceClient spendClient() {
        return SPEND_CLIENT;
    }

    public static UserdataServiceClient userdataClient() {
        return USERDATA_CLIENT;
    }

    public static GhServiceClient ghClient() {
        return GH_CLIENT;
    }

    public static AuthServiceClient authClient() {
        return AUTH_CLIENT;
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
