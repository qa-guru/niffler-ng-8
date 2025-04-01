package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.retrofit.TestResponseAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SpendService {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(TestResponseAdapterFactory.create())
            .build();

    private final SpendServiceClient spendClient = create(SpendServiceClient.class);

    private SpendService() {
    }

    public static SpendServiceClient client() {
        return Holder.INSTANCE.spendClient;
    }

    private <T> T create(Class<T> apiClass) {
        return retrofit.create(apiClass);
    }

    private static class Holder {
        private static final SpendService INSTANCE = new SpendService();
    }

}
