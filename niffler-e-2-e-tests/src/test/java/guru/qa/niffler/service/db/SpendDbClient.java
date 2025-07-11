package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.currency.CurrencyEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.CurrencyRepository;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    protected static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository;
    private final CurrencyRepository currencyRepository;

    public SpendDbClient(Realization realization) {
        this.spendRepository = realization.getSpendRepository();
        this.currencyRepository = realization.getCurrencyRepository();
    }

    private final XaTransactionTemplate xaTransactionTemplate  = new XaTransactionTemplate(
            CFG.spendJdbcUrl(),
            CFG.currencyJdbcUrl()
    );

    public SpendDbClient() {
        this.spendRepository = Realization.HIBERNATE.getSpendRepository();
        this.currencyRepository = Realization.HIBERNATE.getCurrencyRepository();
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

    @Override
    public List<Currency> getAllCurrencies() {
        return xaTransactionTemplate.execute(() ->
                currencyRepository.getAll()
                        .stream()
                        .map(CurrencyEntity::toGrpc)
                        .toList()
        );
    }

    @Override
    @Step("Exchange {spend} {spendCurrency} to {desiredCurrency}")
    public double exchange(double spend, guru.qa.niffler.grpc.CurrencyValues spendCurrency, guru.qa.niffler.grpc.CurrencyValues desiredCurrency){
        if (spend < 0) {
            throw new IllegalArgumentException("Amount to exchange cannot be negative");
        }
        final List<Currency> list = getAllCurrencies();

        BigDecimal spendInUsd = spendCurrency == guru.qa.niffler.grpc.CurrencyValues.USD
                ? BigDecimal.valueOf(spend)
                : BigDecimal.valueOf(spend).multiply(courseForCurrency(spendCurrency, list));

        return spendInUsd.divide(
                courseForCurrency(desiredCurrency, list),
                2,
                RoundingMode.HALF_UP
        )
                .doubleValue();
    }
    private @Nonnull BigDecimal courseForCurrency(CurrencyValues currency, List<Currency> currencyRates) {
        final double currencyRate = currencyRates.stream()
                .filter(cr -> cr.getCurrency().name().equals(currency.name()))
                .findFirst()
                .orElseThrow()
                .getCurrencyRate();

        if(currencyRate <= 0) throw new ArithmeticException("currency rate should be positive");

        return BigDecimal.valueOf(currencyRate);
    }
}