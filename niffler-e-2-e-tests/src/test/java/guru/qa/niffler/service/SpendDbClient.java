package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TransactionIsolation;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.DataBases.dataSource;
import static guru.qa.niffler.data.DataBases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpringJdbcSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);

        CategoryEntity categoryEntity = new CategoryDaoSpringJdbc(
                dataSource(CFG.spendJdbcUrl())).create(CategoryEntity.fromJson(spend.category()));

        spendEntity.setCategory(categoryEntity);

        return SpendJson.fromEntity(
                new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).create(spendEntity));
    }

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
                },
                CFG.spendJdbcUrl(), TransactionIsolation.READ_UNCOMMITTED
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
                    return CategoryJson.fromEntity(
                            new CategoryDaoJdbc(connection).create(CategoryEntity.fromJson(category)));
                },
                CFG.spendJdbcUrl(), TransactionIsolation.READ_UNCOMMITTED);
    }

    public void deleteCategory(CategoryJson category) {
        transaction(connection -> {
                    new CategoryDaoJdbc(connection).deleteCategory(CategoryEntity.fromJson(category));
                },
                CFG.spendJdbcUrl());
    }

    public void deleteSpend(SpendJson spend) {
        transaction(connection -> {
                    new SpendDaoJdbc(connection).deleteSpend(SpendEntity.fromJson(spend));
                },
                CFG.spendJdbcUrl());
    }

    public CategoryJson findCategoryById(UUID id) {
        Optional<CategoryEntity> categoryEntity = new CategoryDaoSpringJdbc(
                dataSource(CFG.spendJdbcUrl())).findCategoryById(id);
        return CategoryJson.fromEntity(categoryEntity);
    }
}
