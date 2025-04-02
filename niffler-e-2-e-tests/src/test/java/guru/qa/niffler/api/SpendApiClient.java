package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
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

  public SpendJson createSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public SpendJson updateSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public SpendJson getSpendById(String id, String username) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id, username).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<SpendJson> getAllSpends(String username, String filterCurrency, Date from, Date to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getAllSpends(username, filterCurrency, from, to).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public void deleteSpends(String username, List<String> ids) {
    final Response<Response<Void>> response;
    try {
      response = spendApi.deleteSpend(username, ids).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  public CategoryJson createCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<CategoryJson> getAllCategories(String username, boolean excludeArchived) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.getAllCategories(username, excludeArchived).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
}
