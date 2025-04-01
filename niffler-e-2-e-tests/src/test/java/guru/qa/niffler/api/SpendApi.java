package guru.qa.niffler.api;

import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface SpendApi {

  @POST("internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spend);
  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);
  @GET("internal/spends/{id}")
  Call<SpendJson> getSpend();
  @GET("internal/spends/all")
  Call<SpendJson> getSpends();
  @DELETE("internal/spends/remove")
  Call<SpendJson> deleteSpend();
  @POST("internal/categories/add")
  Call<SpendJson> addCategorie();
  @POST("internal/categories/update")
  Call<SpendJson> updateCategorie();
  @GET("internal/categories/all")
  Call<SpendJson> getCategories();
}
