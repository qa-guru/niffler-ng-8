package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    SpendEntity create(SpendEntity entity);

    SpendEntity update(SpendEntity entity);

    CategoryEntity create(CategoryEntity entity);

    CategoryEntity update(CategoryEntity entity);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name);

    Optional<SpendEntity> findById(UUID id);

    Optional<SpendEntity> findByUsernameAndDescription(String username, String description);

    boolean delete(SpendEntity entity);

    boolean delete(CategoryEntity entity);

}
