package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.Optional;


public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

//  private final CategoryDao categoryDao = new CategoryDaoJdbc();
//  private final SpendDao spendDao = new SpendDaoJdbc();
  private final SpendRepository spendRepository = new SpendRepositoryHibernate();
//    private final SpendRepository spendRepository = new SpendRepositoryJdbc();
//    private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

  @Override
  public SpendJson createSpend(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
              Optional<CategoryEntity> existingCategory = spendRepository.findCategoryByUsernameAndCategoryName(
                      spendEntity.getUsername(),
                      spendEntity.getCategory().getName()
              );

              if (existingCategory.isPresent()) {
                  spendEntity.setCategory(existingCategory.get());
              } else {
                  CategoryEntity newCategory = spendRepository.createCategory(spendEntity.getCategory());
                  spendEntity.setCategory(newCategory);
              }
          }
          return SpendJson.fromEntity(
                  spendRepository.create(spendEntity)
          );
        }
    );
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    Optional<CategoryEntity> existingCategory = spendRepository
                            .findCategoryByUsernameAndCategoryName(category.username(), category.name());

                    if (existingCategory.isPresent()) {
                        return CategoryJson.fromEntity(existingCategory.get());
                    }
                    CategoryEntity createdCategory = spendRepository
                            .createCategory(CategoryEntity.fromJson(category));
                    return CategoryJson.fromEntity(createdCategory);
                }
        );
    }

    @Override
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            CategoryEntity existingCategory = spendRepository.findCategoryById(category.id())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + category.id()));

            spendRepository.removeCategory(existingCategory);
            return null;
        });
    }
}
