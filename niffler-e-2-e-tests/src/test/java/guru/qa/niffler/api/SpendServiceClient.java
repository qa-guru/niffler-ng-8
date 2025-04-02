package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.*;

import java.util.List;

public interface SpendServiceClient {

    @GET("internal/spends/{id}")
    TestResponse<SpendJson, Void> getSpendById(@Path("id") String id,
                                               @Query("username") String username);

    @GET("internal/spends/all")
    TestResponse<List<SpendJson>, Void> getAllSpends(@Query("username") String username,
                                               @Query("filterCurrency") CurrencyValues cur,
                                               @Query("from") String from,
                                               @Query("to") String to);

    @POST("internal/spends/add")
    TestResponse<SpendJson, Void> addSpend(@Body SpendJson spend);

    @PATCH("internal/spends/edit")
    TestResponse<SpendJson, Void> updateSpend(@Body SpendJson spend);

    @DELETE("internal/spends/remove")
    TestResponse<Void, Void> deleteSpends(@Query("username") String username,
                                          @Query("ids") List<String> ids);

    @POST("internal/categories/add")
    TestResponse<CategoryJson, Void> addCategory(@Body CategoryJson category);

    @PATCH("internal/categories/update")
    TestResponse<CategoryJson, Void> updateCategory(@Body CategoryJson category);

    @GET("internal/categories/all")
    TestResponse<List<CategoryJson>, Void> getAllCategories(@Query("username") String username,
                                                            @Query("excludeArchived") Boolean excludeArchived);

}
