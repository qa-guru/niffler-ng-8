package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;

import java.util.Optional;
import java.util.UUID;

public interface SpendClient {

    SpendJson createSpend(SpendJson spendJson);

    Optional<SpendJson> findSpendById(UUID id);

    boolean deleteSpend(SpendJson spendJson);

    CategoryJson createCategory(CategoryJson categoryJson);

    CategoryJson updateCategory(CategoryJson categoryJson);

    boolean deleteCategory(CategoryJson categoryJson);

}
