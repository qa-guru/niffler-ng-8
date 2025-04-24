package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;


public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  public SpendJson createSpend(SpendJson spend) {
    return jdbcTxTemplate.execute(() -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(
              spendDao.create(spendEntity)
          );
        }
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
