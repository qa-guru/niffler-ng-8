package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.spend.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity create(CategoryEntity entity);

    CategoryEntity update(CategoryEntity entity);

    Optional<CategoryEntity> findById(UUID id);

    Optional<CategoryEntity> findByNameAndUsername(String name, String username);

    List<CategoryEntity> findAllByUsername(String username);

    boolean delete(CategoryEntity entity);

    List<CategoryEntity> findAll();

}