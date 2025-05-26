package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;

import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private final SpendDaoSpringJdbc spendDao = new SpendDaoSpringJdbc();
    private final CategoryDaoSpringJdbc categoryDao = new CategoryDaoSpringJdbc();

  @Override
  public SpendEntity create(SpendEntity spend) {
    final UUID categoryId = spend.getCategory().getId();
    if (categoryId == null && categoryDao.findCategoryById(categoryId).isEmpty()) {
      spend.setCategory(
          categoryDao.create(spend.getCategory())
      );
    }
    return spendDao.create(spend);
  }

  @Override
  public SpendEntity update(SpendEntity spend) {
    spendDao.update(spend);
    categoryDao.update(spend.getCategory());
    return spend;
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.create(category);
  }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    return spendDao.findSpendById(id);
  }

  @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return spendDao.findByUsernameAndDescription(username, description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.deleteCategory(category);
    }

}
