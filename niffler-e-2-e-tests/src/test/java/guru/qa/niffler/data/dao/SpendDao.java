package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    CategoryEntity create(SpendEntity spend);
    Optional<CategoryEntity> findSpendById(UUID id);
    Optional<CategoryEntity> findSpendByUsernameAndCategoryName(String username);
    List<CategoryEntity> findAllSpendByUsername(String username);
    void deleteSpend(SpendEntity spend);
}
