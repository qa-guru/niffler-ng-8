package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SpendDbClient extends AbstractDbClient {

    private static final String SPEND_DB_URL = CFG.spendJdbcUrl();
    private final SpendDao spendDao = new SpendDaoJdbc(SPEND_DB_URL);
    private final CategoryDao categoryDao = new CategoryDaoJdbc(SPEND_DB_URL);

    public SpendJson createSpend(SpendJson spendJson) {
        return spendDbTransaction(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            final CategoryEntity entityCategory = spendEntity.getCategory();
            if (entityCategory.getId() == null) {
                CategoryEntity existCategory = findOrCreateCategory(entityCategory);
                spendEntity.setCategory(existCategory);
            }
            SpendEntity createdSpend = spendDao.createSpend(spendEntity);
            return SpendJson.fromEntity(createdSpend);
        });
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
        spendDbTransaction(() -> {
            SpendEntity spendEntity = getSpendById(spendJson.id());
            spendDao.deleteSpend(spendEntity);
            CategoryEntity entityCategory = spendEntity.getCategory();
            List<SpendEntity> allSpendByCategoryId = spendDao.findAllSpendByCategoryId(entityCategory.getId());
            if (allSpendByCategoryId.isEmpty()) {
                categoryDao.deleteCategory(entityCategory);
            }
        });
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

    private <T> T spendDbTransaction(Supplier<T> supplier) {
        return transaction(supplier, SPEND_DB_URL);
    }

    private void spendDbTransaction(Runnable runnable) {
        transaction(runnable, SPEND_DB_URL);
    }

}
