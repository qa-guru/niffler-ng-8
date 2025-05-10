package guru.qa.niffler.db.repository.impl.hibernate;

import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;
import guru.qa.niffler.db.repository.SpendRepository;

import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryHibernate extends AbstractRepositoryHibernate implements SpendRepository {

    private static final Class<SpendEntity> SPEND_CLASS = SpendEntity.class;
    private static final Class<CategoryEntity> CATEGORY_CLASS = CategoryEntity.class;

    public SpendRepositoryHibernate() {
        super(CFG.spendJdbcUrl());
    }

    @Override
    public SpendEntity create(SpendEntity entity) {
        return super.create(entity);
    }

    @Override
    public SpendEntity update(SpendEntity entity) {
        return super.update(entity);
    }

    @Override
    public CategoryEntity create(CategoryEntity entity) {
        return super.create(entity);
    }

    @Override
    public CategoryEntity update(CategoryEntity entity) {
        return super.update(entity);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return findByIdOpt(CATEGORY_CLASS, id);
    }

    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        String sql = "SELECT c FROM CategoryEntity c WHERE c.username = ?1 AND c.name = ?2";
        return findSingleResultOpt(CATEGORY_CLASS, sql, username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return findByIdOpt(SPEND_CLASS, id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        String sql = "SELECT s FROM SpendEntity s WHERE s.username = ?1 AND s.description = ?2";
        return findSingleResultOpt(SPEND_CLASS, sql, username, description);
    }

    @Override
    public boolean delete(SpendEntity entity) {
        return delete(SPEND_CLASS, entity.getId());
    }

    @Override
    public boolean delete(CategoryEntity entity) {
        return delete(CATEGORY_CLASS, entity.getId());
    }

}
