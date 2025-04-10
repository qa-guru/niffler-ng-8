package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.spend.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity createCategory(CategoryEntity entity);

    CategoryEntity updateCategory(CategoryEntity entity);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByNameAndUsername(String name, String username);

    List<CategoryEntity> findAllCategoryByUsername(String username);

    boolean deleteCategory(CategoryEntity entity);

}