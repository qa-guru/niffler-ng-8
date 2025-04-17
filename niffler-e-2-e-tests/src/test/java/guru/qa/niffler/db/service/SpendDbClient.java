package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.CategoryDaoSpringJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.SpendDaoSpringJdbc;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;
import guru.qa.niffler.db.tpl.JdbcTransactionTemplate;

import java.util.List;
import java.util.UUID;

public class SpendDbClient extends AbstractDbClient {

    private static final String SPEND_DB_URL = CFG.spendJdbcUrl();
    private final SpendDao spendDao = new SpendDaoSpringJdbc(SPEND_DB_URL);
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc(SPEND_DB_URL);
    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(SPEND_DB_URL);

    public SpendJson createSpend(SpendJson spendJson) {
        return jdbcTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            final CategoryEntity entityCategory = spendEntity.getCategory();
            if (entityCategory.getId() == null) {
                CategoryEntity existCategory = findOrCreateCategory(entityCategory);
                spendEntity.setCategory(existCategory);
            }
            SpendEntity createdSpend = spendDao.create(spendEntity);
            return SpendJson.fromEntity(createdSpend);
        });
    }

    public CategoryEntity findOrCreateCategory(CategoryEntity entityCategory) {
        return categoryDao.findByNameAndUsername(entityCategory.getName(), entityCategory.getUsername())
                .orElseGet(() -> categoryDao.create(entityCategory));
    }

    public SpendJson findSpendById(UUID id) {
        SpendEntity spendEntity = getSpendById(id);
        return SpendJson.fromEntity(spendEntity);
    }

    public void deleteSpend(SpendJson spendJson) {
        jdbcTxTemplate.execute(() -> {
            SpendEntity spendEntity = getSpendById(spendJson.id());
            spendDao.delete(spendEntity);
            CategoryEntity entityCategory = spendEntity.getCategory();
            List<SpendEntity> allSpendByCategoryId = spendDao.findAllByCategoryId(entityCategory.getId());
            if (allSpendByCategoryId.isEmpty()) {
                categoryDao.delete(entityCategory);
            }
        });
    }

    private SpendEntity getSpendById(UUID id) {
        return spendDao.findById(id)
                .map(entity -> {
                    UUID categoryId = entity.getCategory().getId();
                    CategoryEntity categoryEntity = categoryDao.findById(categoryId)
                            .orElseThrow(() -> new IllegalStateException("У 'трат' отсутствует категория"));
                    entity.setCategory(categoryEntity);
                    return entity;
                })
                .orElseThrow(() -> new IllegalStateException("'Трата' не найдена по id " + id));
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        CategoryEntity createdCategory = categoryDao.create(categoryEntity);
        return CategoryJson.fromEntity(createdCategory);
    }

    public CategoryJson updateCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        CategoryEntity createdCategory = categoryDao.update(categoryEntity);
        return CategoryJson.fromEntity(createdCategory);
    }

    public boolean deleteCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        return categoryDao.delete(categoryEntity);
    }

}
