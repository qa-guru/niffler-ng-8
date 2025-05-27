package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;


@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    protected static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository;

    public SpendDbClient(Realization realization) {
        this.spendRepository = realization.getSpendRepository();
    }

    private final XaTransactionTemplate xaTransactionTemplate  = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public SpendDbClient() {
        this.spendRepository = Realization.HIBERNATE.getSpendRepository();
    }

    @Override
    @Step("Create spend")
    public SpendJson createSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity entity = SpendEntity.fromJson(spend);
            return SpendJson.fromEntity(spendRepository.create(entity));
        });
    }


    @Override
    @Step("Delete spend")
    public void deleteSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(
                    SpendEntity.fromJson(spend)
            );
            return null;
        });
    }

    @Override
    @Step("Create category")
    public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(
                spendRepository.createCategory(
                        CategoryEntity.fromJson(category)
                ))
        );
    }

    @Override
    @Step("Delete category")
    public void deleteCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(
                    CategoryEntity.fromJson(category)
            );
            return null;
        });
    }

    @Override
    @Step("Find spend by id {spendId}")
    public Optional<SpendJson> findById(UUID spendId) {
        return xaTransactionTemplate.execute(() ->
                spendRepository.findById(spendId)
                        .map(SpendJson::fromEntity)
        );
    }

}