package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.Connection;
import java.util.Optional;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity category = spendEntity.getCategory();

                        Optional<CategoryEntity> existingCategory = new CategoryDaoJdbc(connection)
                                .findCategoryByUsernameAndCategoryName(
                                        category.getUsername(),
                                        category.getName()
                                );
                        if (existingCategory.isPresent()) {
                            spendEntity.setCategory(existingCategory.get());
                        } else {
                            CategoryEntity newCategory = new CategoryDaoJdbc(connection)
                                    .create(category);
                            spendEntity.setCategory(newCategory);
                        }
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                },
                CFG.spendJdbcUrl(),
                Connection.TRANSACTION_READ_UNCOMMITTED
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
                    Optional<CategoryEntity> existingCategory = new CategoryDaoJdbc(connection)
                            .findCategoryByUsernameAndCategoryName(category.username(), category.name());

                    if (existingCategory.isPresent()) {
                        return CategoryJson.fromEntity(existingCategory.get());
                    }
                    CategoryEntity createdCategory = new CategoryDaoJdbc(connection)
                            .create(CategoryEntity.fromJson(category));
                    return CategoryJson.fromEntity(createdCategory);
                },
                CFG.spendJdbcUrl(),
                Connection.TRANSACTION_READ_UNCOMMITTED
        );
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(connection -> {
                    new CategoryDaoJdbc(connection).findById(category.id())
                            .orElseThrow(() -> new RuntimeException("Category not found: " + category.id()));
                    CategoryEntity updatedCategory = new CategoryDaoJdbc(connection)
                            .update(CategoryEntity.fromJson(category));
                    return CategoryJson.fromEntity(updatedCategory);
                },
                CFG.spendJdbcUrl(),
                Connection.TRANSACTION_READ_UNCOMMITTED
        );

    }


}
