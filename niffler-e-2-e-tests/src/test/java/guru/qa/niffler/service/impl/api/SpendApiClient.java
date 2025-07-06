package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.SpendEndpointClient;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.retrofit.TestResponse;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendApiClient extends AbstractApiClient implements SpendClient {

    public final SpendEndpointClient spendClient = ApiClients.spendClient();

    private SpendApiClient() {
    }

    public static SpendApiClient client() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final SpendApiClient INSTANCE = new SpendApiClient();
    }

    @Step("Создание трат")
    @Override
    public @Nullable SpendJson createSpend(SpendJson spendJson) {
        TestResponse<SpendJson, Void> response = spendClient.addSpend(spendJson);
        return validateSuccessAndMapObj(response);
    }

    @Step("Поиск трат по id")
    @Override
    public @Nonnull Optional<SpendJson> findSpendById(UUID id) {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    @Step("Удаление трат")
    @Override
    public boolean deleteSpend(SpendJson spendJson) {
        TestResponse<Void, Void> response = spendClient.deleteSpends(spendJson.username(), List.of(spendJson.id().toString()));
        if (response.isSuccessful()) {
            return true;
        } else {
            return false;
        }
    }

    @Step("Создание категории трат")
    @Override
    public @Nullable CategoryJson createCategory(CategoryJson categoryJson) {
        TestResponse<CategoryJson, Void> response = spendClient.addCategory(categoryJson);
        return validateSuccessAndMapObj(response);
    }

    @Step("Обновление категории трат")
    @Override
    public @Nullable CategoryJson updateCategory(CategoryJson categoryJson) {
        TestResponse<CategoryJson, Void> response = spendClient.updateCategory(categoryJson);
        return validateSuccessAndMapObj(response);
    }

    @Step("Удаление категории трат")
    @Override
    public boolean deleteCategory(CategoryJson categoryJson) {
        throw new UnsupportedOperationException("Метод не реализован");
    }
}
