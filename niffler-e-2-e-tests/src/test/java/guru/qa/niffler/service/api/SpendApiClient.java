package guru.qa.niffler.service.api;

import guru.qa.niffler.utils.SuccessRequestExecutor;
import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendApiClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SuccessRequestExecutor sre = new SuccessRequestExecutor();
  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  public SpendJson editSpend(SpendJson spend) {
    return sre.executeRequest(spendApi.editSpend(spend));
  }

  public SpendJson getSpend(String id, String username) {
    return sre.executeRequest(spendApi.getSpend(id, username));
  }

  public List<SpendJson> getSpends(String username, CurrencyValues currencyValues, Date from, Date to) {
    return sre.executeRequest(spendApi.getSpends(username, currencyValues, from, to));
  }

  public Response<Void> deleteSpends(String username, List<String> ids) {
    return sre.executeRequest(spendApi.deleteSpends(username, ids));
  }

  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    return sre.executeRequest(spendApi.getCategories(username, excludeArchived));
  }

  public CategoryJson updateCategory(CategoryJson category) {
    return sre.executeRequest(spendApi.updateCategory(category));
  }

  @Override
  public SpendJson createSpend(SpendJson spend) {
    return sre.executeRequest(spendApi.addSpend(spend));
  }

  @Override
  public void deleteSpend(SpendJson spend) {
    deleteSpends(spend.username(),List.of(
            spend.id().toString()
    ));
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    return sre.executeRequest(spendApi.addCategory(category));
  }

  @Override
  public void deleteCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Method 'deleteCategory' is not implemented");
  }

  @Override
  public Optional<SpendJson> findById(UUID spendId) {
    throw new UnsupportedOperationException("Method 'findCategoryById' is not implemented");
  }
}
