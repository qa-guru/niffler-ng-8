package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.SpendDaoSpringJdbc;
import guru.qa.niffler.data.dao.interfaces.CategoryDao;
import guru.qa.niffler.data.dao.interfaces.SpendDao;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TransactionIsolation;

import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private CategoryDao categoryDao = new CategoryDaoJdbc();
    private SpendDao spendDao = new SpendDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);

        CategoryEntity categoryEntity = categoryDao.create(CategoryEntity.fromJson(spend.category()));

        spendEntity.setCategory(categoryEntity);

        return SpendJson.fromEntity(
                spendDao.create(spendEntity));
    }

    public SpendJson createTxSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendDao.create(spendEntity));
                },
                TransactionIsolation.READ_UNCOMMITTED
        );
    }

    public CategoryJson createTxCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> CategoryJson.fromEntity(
                        categoryDao.create(CategoryEntity.fromJson(category))),
                TransactionIsolation.READ_UNCOMMITTED);
    }

    public void deleteTxCategory(CategoryJson category) {
        jdbcTxTemplate.executeVoid(() -> {
                    categoryDao.deleteCategory(CategoryEntity.fromJson(category));
                },
                TransactionIsolation.READ_UNCOMMITTED);
    }

    public void deleteTxSpend(SpendJson spend) {
        jdbcTxTemplate.executeVoid(() -> {
                    spendDao.deleteSpend(SpendEntity.fromJson(spend));
                }, TransactionIsolation.READ_UNCOMMITTED
        );
    }

    public CategoryJson findCategoryById(UUID id) {
        Optional<CategoryEntity> categoryEntity = categoryDao.findCategoryById(id);
        return CategoryJson.fromEntity(categoryEntity);
    }
}