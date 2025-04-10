package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.db.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.util.List;
import java.util.UUID;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spendJson) {
        SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
        final CategoryEntity entityCategory = spendEntity.getCategory();
        if (entityCategory.getId() == null) {
            CategoryEntity existCategory = findOrCreateCategory(entityCategory);
            spendEntity.setCategory(existCategory);
        }
        SpendEntity createdSpend = spendDao.createSpend(spendEntity);
        return SpendJson.fromEntity(createdSpend);
    }

    public CategoryEntity findOrCreateCategory(CategoryEntity entityCategory) {
        return categoryDao.findCategoryByNameAndUsername(entityCategory.getName(), entityCategory.getUsername())
                .orElseGet(() -> categoryDao.createCategory(entityCategory));
    }

    public SpendJson findSpendById(UUID id) {
        SpendEntity spendEntity = getSpendById(id);
        return SpendJson.fromEntity(spendEntity);
    }

    public void deleteSpend(SpendJson spendJson) {
        SpendEntity spendEntity = getSpendById(spendJson.id());
        spendDao.deleteSpend(spendEntity);
        CategoryEntity entityCategory = spendEntity.getCategory();
        List<SpendEntity> allSpendByCategoryId = spendDao.findAllSpendByCategoryId(entityCategory.getId());
        if (allSpendByCategoryId.isEmpty()) {
            categoryDao.deleteCategory(entityCategory);
        }
    }

    private SpendEntity getSpendById(UUID id) {
        return spendDao.findSpendById(id)
                .map(entity -> {
                    UUID categoryId = entity.getCategory().getId();
                    CategoryEntity categoryEntity = categoryDao.findCategoryById(categoryId)
                            .orElseThrow(() -> new IllegalStateException("У 'трат' отсутствует категория"));
                    entity.setCategory(categoryEntity);
                    return entity;
                })
                .orElseThrow(() -> new IllegalStateException("'Трата' не найдена по id " + id));
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        CategoryEntity createdCategory = categoryDao.createCategory(categoryEntity);
        return CategoryJson.fromEntity(createdCategory);
    }

    public CategoryJson updateCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        CategoryEntity createdCategory = categoryDao.updateCategory(categoryEntity);
        return CategoryJson.fromEntity(createdCategory);
    }

    public boolean deleteCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        return categoryDao.deleteCategory(categoryEntity);
    }

}
