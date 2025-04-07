package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.retrofit.TestResponseAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiBaseClient {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    protected static final Config CFG = Config.getInstance();
    protected final Retrofit retrofit;

    protected ApiBaseClient(String baseUrl) {
        this.retrofit = buildRetrofit(baseUrl);
    }

    private Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(TestResponseAdapterFactory.create())
                .build();
    }

    protected <T> T create(Class<T> apiClass) {
        return retrofit.create(apiClass);
    }

}
