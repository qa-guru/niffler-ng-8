package guru.qa.niffler.service.api;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.utils.SuccessRequestExecutor;
import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient implements SpendClient {

  private static final Config CFG = Config.getInstance();
  private final SuccessRequestExecutor sre = new SuccessRequestExecutor();
  private final SpendApi spendApi;

  public SpendApiClient(){
    super(CFG.spendUrl());
    this.spendApi = create(SpendApi.class);
  }

  @Step("Edit spend")
  public SpendJson editSpend(SpendJson spend) {
    return sre.executeRequest(spendApi.editSpend(spend));
  }

  @Step("Get spend by id {id} and username {username}")
  public SpendJson getSpend(String id, String username) {
    return sre.executeRequest(spendApi.getSpend(id, username));
  }

  @Step("Get spends by username {username}, currency value {currencyValues}, date from {from} to {to}")
  public List<SpendJson> getSpends(String username, @Nullable CurrencyValues currencyValues, @Nullable Date from, @Nullable Date to) {
    return sre.executeRequest(spendApi.getSpends(username, currencyValues, from, to));
  }

  @Step("Delete spend by id's {ids} and username {username}")
  public Response<Void> deleteSpends(String username,List<String> ids) {
    return sre.executeRequest(spendApi.deleteSpends(username, ids));
  }

  @Step("Get categories by username {username} with exclude archived {excludeArchived}")
  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    return sre.executeRequest(spendApi.getCategories(username, excludeArchived));
  }

  @Step("Update category")
  public CategoryJson updateCategory(CategoryJson category) {
    return sre.executeRequest(spendApi.updateCategory(category));
  }

  @Override
  @Step("Create spend")
  public SpendJson createSpend(SpendJson spend) {
    return sre.executeRequest(spendApi.addSpend(spend));
  }

  @Override
  @Step("Delete spend")
  public void deleteSpend(SpendJson spend) {
    deleteSpends(spend.username(),List.of(
            spend.id().toString()
    ));
  }

  @Override
  @Step("Create category")
  public CategoryJson createCategory(CategoryJson category) {
    return sre.executeRequest(spendApi.addCategory(category));
  }

  @Override
  @Step("Delete category")
  public void deleteCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Method 'deleteCategory' is not implemented");
  }

  @Override
  @Step("Find spend by id {spendId}")
  public Optional<SpendJson> findById(UUID spendId) {
    throw new UnsupportedOperationException("Method 'findCategoryById' is not implemented");
  }
}
