package guru.qa.niffler.db.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;
import guru.qa.niffler.db.repository.SpendRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {

    protected static final String JDBC_URL = Config.getInstance().spendJdbcUrl();
    public final SpendDao spendDao = new SpendDaoJdbc(JDBC_URL);
    public final CategoryDao categoryDao = new CategoryDaoJdbc(JDBC_URL);

    @Override
    public @Nonnull SpendEntity create(SpendEntity entity) {
        CategoryEntity entityCategory = entity.getCategory();
        if (entityCategory.getId() == null) {
            entityCategory = findOrCreateCategory(entityCategory);
            entity.setCategory(entityCategory);
        }
        return spendDao.create(entity).setCategory(entityCategory);
    }

    public @Nonnull CategoryEntity findOrCreateCategory(CategoryEntity entityCategory) {
        return categoryDao.findByNameAndUsername(entityCategory.getName(), entityCategory.getUsername())
            .orElseGet(() -> create(entityCategory));
    }

    @Override
    public @Nonnull SpendEntity update(SpendEntity entity) {
        update(entity.getCategory());
        entity = spendDao.update(entity);
        return entity;
    }

    @Override
    public @Nonnull CategoryEntity create(CategoryEntity entity) {
        return categoryDao.create(entity);
    }

    @Override
    public @Nonnull CategoryEntity update(CategoryEntity entity) {
        return categoryDao.update(entity);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        return categoryDao.findByNameAndUsername(name, username);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(UUID id) {
        return spendDao.findById(id)
            .map(entity -> {
                UUID categoryId = entity.getCategory().getId();
                CategoryEntity categoryEntity = categoryDao.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("У 'трат' отсутствует категория"));
                entity.setCategory(categoryEntity);
                return entity;
            });
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        return spendDao.findByUsernameAndDescription(username, description)
            .map(e -> {
                categoryDao.findById(e.getCategory().getId())
                    .map(e::setCategory);
                return e;
            });
    }

    @Override
    public boolean delete(SpendEntity entity) {
        Optional<SpendEntity> spendById = findById(entity.getId());
        boolean result = false;
        if (spendById.isPresent()) {
            entity = spendById.get();
            result = spendDao.delete(entity);
            CategoryEntity entityCategory = entity.getCategory();
            List<SpendEntity> allSpendByCategoryId = spendDao.findAllByCategoryId(entityCategory.getId());
            if (allSpendByCategoryId.isEmpty()) {
                delete(entityCategory);
            }
        }
        return result;
    }

    @Override
    public boolean delete(CategoryEntity entity) {
        return categoryDao.delete(entity);
    }

}
