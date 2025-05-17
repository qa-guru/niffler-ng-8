package guru.qa.niffler.service.impl.db;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;
import guru.qa.niffler.db.repository.SpendRepository;
import guru.qa.niffler.db.repository.impl.jdbc.SpendRepositoryJdbc;
import guru.qa.niffler.db.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.service.SpendClient;

import java.util.Optional;
import java.util.UUID;

public class SpendDbClient extends AbstractDbClient implements SpendClient {

    private static final String SPEND_DB_URL = CFG.spendJdbcUrl();
    private final SpendRepository spendRepository = new SpendRepositoryJdbc();
    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(SPEND_DB_URL);

    public SpendJson createSpend(SpendJson spendJson) {
        return jdbcTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            SpendEntity createdSpend = spendRepository.create(spendEntity);
            return SpendJson.fromEntity(createdSpend);
        });
    }

    public Optional<SpendJson> findSpendById(UUID id) {
        return spendRepository.findById(id)
            .map(SpendJson::fromEntity);
    }

    public boolean deleteSpend(SpendJson spendJson) {
        return jdbcTxTemplate.execute(() -> {
            SpendEntity entity = SpendEntity.fromJson(spendJson);
            return spendRepository.delete(entity);
        });
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        CategoryEntity createdCategory = spendRepository.create(categoryEntity);
        return CategoryJson.fromEntity(createdCategory);
    }

    public CategoryJson updateCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        CategoryEntity createdCategory = spendRepository.update(categoryEntity);
        return CategoryJson.fromEntity(createdCategory);
    }

    public boolean deleteCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        return spendRepository.delete(categoryEntity);
    }

}
