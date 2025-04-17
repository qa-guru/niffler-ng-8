package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();
  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public SpendJson createSpend(SpendJson spend) {
    return transaction(connection -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                .create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(
              new SpendDaoJdbc(connection).create(spendEntity)
          );
        },
        CFG.spendJdbcUrl()
    );
  }

  public CategoryJson createCategory(CategoryJson category) {

    Optional<CategoryEntity> existingCategory = categoryDao
            .findCategoryByUsernameAndCategoryName(category.username(),category.name());

    if (existingCategory.isPresent()){
      return CategoryJson.fromEntity(existingCategory.get());
    }

    CategoryEntity createdCategory = categoryDao.create(CategoryEntity.fromJson(category));

    return CategoryJson.fromEntity(createdCategory);
  }

  public CategoryJson updateCategory(CategoryJson category) {

    categoryDao.findById(category.id())
            .orElseThrow(() -> new RuntimeException("Category not found: " + category.id()));

    CategoryEntity updatedCategory = categoryDao.update(CategoryEntity.fromJson(category));

    return CategoryJson.fromEntity(updatedCategory);
  }


}
