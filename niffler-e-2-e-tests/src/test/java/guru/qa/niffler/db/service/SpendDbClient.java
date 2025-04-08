package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.db.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spendJson) {
        SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity createdCategory = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(createdCategory);
        }
        SpendEntity createdSpend = spendDao.create(spendEntity);
        return SpendJson.fromEntity(createdSpend);
    }
}
