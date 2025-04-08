package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.spend.CategoryEntity;

import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity create(CategoryEntity entity);

    Optional<CategoryEntity> findCategoryById(UUID id);

}