package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositorySpringJdbc implements SpendRepository {

  private static final Config CFG = Config.getInstance();

  private final String url = CFG.spendJdbcUrl();
  private final SpendDao spendDao = new SpendDaoSpringJdbc();
  private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    final UUID categoryId = spend.getCategory().getId();
    if (categoryId == null || categoryDao.findById(categoryId).isEmpty()) {
      spend.setCategory(
          categoryDao.create(spend.getCategory())
      );
    }
    return spendDao.create(spend);
  }

  @Nonnull
  @Override
  public SpendEntity update(SpendEntity spend) {
    spendDao.update(spend);
    categoryDao.update(spend.getCategory());
    return spend;
  }

  @Nonnull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.create(category);
  }

  @Nonnull
  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    categoryDao.update(category);
    return category;
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return categoryDao.findById(id);
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              "SELECT * FROM category WHERE username = ? and name = ?",
              CategoryEntityRowMapper.instance,
              username,
              name
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    return spendDao.findById(id);
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              "SELECT * FROM spend WHERE username = ? and description = ?",
              SpendEntityRowMapper.instance,
              username,
              description
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spend.getId());
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update("DELETE FROM category WHERE id = ?", category.getId());
  }
}
