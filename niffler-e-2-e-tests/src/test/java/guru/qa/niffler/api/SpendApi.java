package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface SpendApi {

  @POST("internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spend);
  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);
  @GET("internal/spends/{id}")
  Call<SpendJson> getSpend(@Path("id") String id);
  @GET("internal/spends/all")
  Call<SpendJson> getSpends();
  @DELETE("internal/spends/remove")
  Call<SpendJson> deleteSpend(@Query("ids") String ids);
  @POST("internal/categories/add")
  Call<CategoryJson> addCategory(@Body CategoryJson category);
  @PATCH("internal/categories/update")
  Call<CategoryJson> updateCategory(@Body CategoryJson category);
  @GET("internal/categories/all")
  Call<CategoryJson> getCategories();
}
