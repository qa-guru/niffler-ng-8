package guru.qa.niffler.service.db.dao;

import guru.qa.niffler.data.dao.*;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.db.SpendDbClient;

public abstract class SpendDaoDbClient extends SpendDbClient {

    protected final CategoryDao categoryDao;
    protected final SpendDao spendDao;

    protected SpendDaoDbClient(
            CategoryDao categoryDao,
            SpendDao spendDao){
        this.categoryDao = categoryDao;
        this.spendDao = spendDao;
    }

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            spendDao.create(spendEntity)
                    );
                }
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        return CategoryJson.fromEntity(
                categoryDao.create(
                        CategoryEntity.fromJson(category)
                )
        );
    }

    public void deleteCategory(CategoryJson category) {
        categoryDao.deleteCategory(
                CategoryEntity.fromJson(category)
        );

    }


    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendDao.deleteSpend(spendEntity);
    }

    public SpendJson createSpendNotAcid(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao
                    .create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(
                spendDao.create(spendEntity)
        );
    }
}
