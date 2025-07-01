package guru.qa.niffler.api;

import guru.qa.niffler.api.core.TradeSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.retrofit.TestResponseAdapterFactory;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import lombok.experimental.UtilityClass;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.net.CookieManager;
import java.net.CookiePolicy;

@UtilityClass
public class ApiClients {

    private static final Config CFG = Config.getInstance();
    private static final SpendServiceClient SPEND_CLIENT = buildClient(CFG.spendUrl(), SpendServiceClient.class);
    private static final UserdataServiceClient USERDATA_CLIENT = buildClient(CFG.userdataUrl(), UserdataServiceClient.class);
    private static final GhServiceClient GH_CLIENT = buildClient(CFG.ghUrl(), GhServiceClient.class);
    private static final AuthServiceClient AUTH_CLIENT = buildClient(CFG.authUrl(), AuthServiceClient.class);

    public static @Nonnull SpendServiceClient spendClient() {
        return SPEND_CLIENT;
    }

    public static @Nonnull UserdataServiceClient userdataClient() {
        return USERDATA_CLIENT;
    }

    public static @Nonnull GhServiceClient ghClient() {
        return GH_CLIENT;
    }

    public static @Nonnull AuthServiceClient authClient() {
        return AUTH_CLIENT;
    }

    private static <T> @Nonnull T buildClient(@Nonnull String baseUrl, @Nonnull Class<T> apiClass) {
        return buildClient(
            baseUrl,
            apiClass,
            JacksonConverterFactory.create(),
            TestResponseAdapterFactory.create(),
            false,
            HttpLoggingInterceptor.Level.BODY
        );
    }

    private static OkHttpClient buildOkHttpClient(boolean followingRedirect,
                                                  HttpLoggingInterceptor.Level logLevel,
                                                  Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .cookieJar(new JavaNetCookieJar(
                new CookieManager(
                    TradeSafeCookieStore.INSTANCE,
                    CookiePolicy.ACCEPT_ALL
                )
            ))
            .followRedirects(followingRedirect)
            .addNetworkInterceptor(
                new AllureOkHttp3()
                    .setRequestTemplate("http-request-custom.ftl")
                    .setResponseTemplate("http-response-custom.ftl")
            );
        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        builder.addNetworkInterceptor(
            new HttpLoggingInterceptor().setLevel(logLevel)
        );
        return builder
            .build();
    }

    private static <T> @Nonnull T buildClient(@Nonnull String baseUrl,
                                              @Nonnull Class<T> apiClass,
                                              @Nonnull Converter.Factory converterFactory,
                                              @Nonnull CallAdapter.Factory adapterFactory,
                                              boolean followingRedirect,
                                              @Nonnull HttpLoggingInterceptor.Level logLevel,
                                              @Nonnull Interceptor... interceptors
    ) {
        OkHttpClient okHttpClient = buildOkHttpClient(followingRedirect, logLevel, interceptors);
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(adapterFactory)
            .build()
            .create(apiClass);
    }
}
