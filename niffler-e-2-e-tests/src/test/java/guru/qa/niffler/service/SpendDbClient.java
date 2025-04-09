package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import lombok.NonNull;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final UserDao userDao = new UserDaoJdbc();

    public SpendJson createSpend(@NonNull SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(
                spendDao.create(spendEntity)
        );
    }

    public void deleteSpend(@NonNull SpendJson spendJson){
        spendDao.deleteSpend(
                SpendEntity.fromJson(spendJson)
        );
    }

    public CategoryJson createCategory(@NonNull CategoryJson category){
        return CategoryJson.fromEntity(
                categoryDao.create(
                        CategoryEntity.fromJson(category)
                )
        );
    }

    public void deleteCategory(@NonNull CategoryJson category){
        categoryDao.deleteCategory(
                CategoryEntity.fromJson(category)
        );
    }
}