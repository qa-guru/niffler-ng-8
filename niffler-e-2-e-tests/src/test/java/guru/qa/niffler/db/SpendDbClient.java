package guru.qa.niffler.db;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.impl.SpendDaoJdbc;
import guru.qa.niffler.model.SpendJson;

public class SpendDbClient {
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendEntity create(SpendJson json) {
        SpendEntity spend = SpendEntity.fromJson(json);
        if (spend.getCategory().getId() == null) {
            CategoryEntity category = categoryDao.create(spend.getCategory());
            spend.setCategory(category);
        }
        return spendDao.create(spend);
    }
}
