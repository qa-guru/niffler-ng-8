package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public class SpendDbClient {

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public SpendJson createSpend(SpendJson spend) {
    SpendEntity spendEntity = SpendEntity.fromJson(spend);
    if (spendEntity.getCategory().getId() == null) {
      CategoryEntity categoryEntity = CategoryEntity.fromJson(createCategory(spend.category()));
      spendEntity.setCategory(categoryEntity);
    }
    return SpendJson.fromEntity(
            spendDao.create(spendEntity)
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
