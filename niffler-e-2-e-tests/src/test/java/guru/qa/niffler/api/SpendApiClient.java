package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

  private <T> T executeRequest(Call<T> call, int successLastDigit) {
    try {
      int successCode = 200+successLastDigit;
      Response<T> response = call.execute();
      assertEquals(successCode, response.code());
      return response.body();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  // Используем универсальный метод для всех операций
  public SpendJson addSpend(SpendJson spend) {
    return executeRequest(spendApi.addSpend(spend),1);
  }

  public SpendJson editSpend(SpendJson spend) {
    return executeRequest(spendApi.editSpend(spend),0);
  }

  public SpendJson getSpend(String id, String username) {
    return executeRequest(spendApi.getSpend(id, username),0);
  }

  public List<SpendJson> getSpends(String username, CurrencyValues currencyValues, Date from, Date to) {
    return executeRequest(spendApi.getSpends(username, currencyValues, from, to),0);
  }

  public Response<Void> deleteSpends(String username, List<String> ids) {
    return executeRequest(spendApi.deleteSpends(username, ids),2);
  }

  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    return executeRequest(spendApi.getCategories(username, excludeArchived),0);
  }

  public CategoryJson addCategory(CategoryJson category) {
    return executeRequest(spendApi.addCategory(category),0);
  }

  public CategoryJson updateCategory(CategoryJson category) {
    return executeRequest(spendApi.updateCategory(category),0);
  }
}
