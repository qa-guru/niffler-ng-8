package guru.qa.niffler.api;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface SpendServiceClient {

    @GET("internal/spends/{id}")
    @Nonnull
    TestResponse<SpendJson, Void> getSpendById(@Path("id") String id,
                                               @Query("username") String username);

    @GET("internal/spends/all")
    @Nonnull
    TestResponse<List<SpendJson>, Void> getAllSpends(@Query("username") String username,
                                                     @Nullable @Query("filterCurrency") CurrencyValues cur,
                                                     @Nullable @Query("from") String from,
                                                     @Nullable @Query("to") String to);

    @POST("internal/spends/add")
    @Nonnull
    TestResponse<SpendJson, Void> addSpend(@Body SpendJson spend);

    @PATCH("internal/spends/edit")
    @Nonnull
    TestResponse<SpendJson, Void> updateSpend(@Body SpendJson spend);

    @DELETE("internal/spends/remove")
    @Nonnull
    TestResponse<Void, Void> deleteSpends(@Query("username") String username,
                                          @Query("ids") List<String> ids);

    @POST("internal/categories/add")
    @Nonnull
    TestResponse<CategoryJson, Void> addCategory(@Body CategoryJson category);

    @PATCH("internal/categories/update")
    @Nonnull
    TestResponse<CategoryJson, Void> updateCategory(@Body CategoryJson category);

    @GET("internal/categories/all")
    @Nonnull
    TestResponse<List<CategoryJson>, Void> getAllCategories(@Query("username") String username,
                                                            @Query("excludeArchived") Boolean excludeArchived);

}
