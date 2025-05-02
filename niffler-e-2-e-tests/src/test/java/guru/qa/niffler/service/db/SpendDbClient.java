package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.Optional;
import java.util.UUID;


public class SpendDbClient implements SpendClient {

    protected static final Config CFG = Config.getInstance();

    protected final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final SpendRepository spendRepository;

    public SpendDbClient(Realization realization) {
        this.spendRepository = realization.getSpendRepository();
    }

    public SpendDbClient() {
        this.spendRepository = Realization.HIBERNATE.getSpendRepository();
    }

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() ->
                SpendJson.fromEntity(
                    spendRepository.create(
                        SpendEntity.fromJson(spend)
                ))
        );
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        jdbcTxTemplate.execute(() -> {
            spendRepository.remove(
                    SpendEntity.fromJson(spend)
            );
            return null;
        });
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() ->
                CategoryJson.fromEntity(
                spendRepository.createCategory(
                        CategoryEntity.fromJson(category)
                ))
        );
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() -> {
            spendRepository.removeCategory(
                    CategoryEntity.fromJson(category)
            );
            return null;
        });
    }

    @Override
    public Optional<SpendJson> findById(UUID spendId) {
        return jdbcTxTemplate.execute(() ->
                spendRepository.findById(spendId)
                        .map(SpendJson::fromEntity)
        );
    }

}