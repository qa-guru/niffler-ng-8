package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);


    public SpendJson addSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        System.out.println(response.body());
        assertEquals(201, response.code());
        return response.body();
    }


    public SpendJson patchSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.patchSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        System.out.println(response.body());
        assertEquals(200, response.code());
        return response.body();
    }


    public CategoryJson addSpendCategories(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addSpendCategories(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }


    public CategoryJson updateCategories(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

}
