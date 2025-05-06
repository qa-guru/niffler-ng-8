package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {
    SpendEntity createSpend(SpendEntity spend);

    SpendEntity updateSpend(SpendEntity spend);

    CategoryEntity createCategory(CategoryEntity category);

    CategoryEntity updateCategory(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUserNameAndCategoryName(String userName, String spendName);

    Optional<SpendEntity> findSpendById(UUID id);

    Optional<SpendEntity> findSpendByUserNameAndSpendDescription(String userName, String spendDescription);

    void removeSpend(SpendEntity spend);

    void removeCategory(CategoryEntity category);
}
