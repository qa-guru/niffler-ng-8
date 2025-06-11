package guru.qa.niffler.db;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.impl.CategoryDaoJdbc;
import guru.qa.niffler.model.CategoryJson;

public class CategoryDbClient {
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public CategoryEntity create(CategoryJson json) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(json.id());
        entity.setName(json.name());
        entity.setUsername(json.username());
        entity.setArchived(json.archived());
        return categoryDao.create(entity);
    }
}
