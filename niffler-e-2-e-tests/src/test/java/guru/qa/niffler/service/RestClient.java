package guru.qa.niffler.service;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();

    protected final Retrofit retrofit;

    private final OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(
            new AllureOkHttp3()
                    .setRequestTemplate("http-request.ftl")
                    .setResponseTemplate("http-response.ftl")
    ).build();

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);


        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .build();
    }

    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }

    public static final class ActionRestClient extends RestClient {

        public ActionRestClient(String baseUrl) {
            super(baseUrl);
        }
    }

    public <T> T execute(Call<T> executeMethod, int expectedCode) {
        final Response<T> response;
        try {
            response = executeMethod.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(expectedCode, response.code());
        return response.body();
    }
}

