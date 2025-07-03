package guru.qa.niffler.api;


import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        this.spendApi = retrofit.create(SpendApi.class);
    }

    public @Nullable SpendJson addSpend(SpendJson spend) {
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


    public @Nullable SpendJson patchSpend(SpendJson spend) {
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


    public @Nullable CategoryJson addSpendCategories(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addSpendCategories(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }


    public @Nullable CategoryJson updateCategories(CategoryJson category) {
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
