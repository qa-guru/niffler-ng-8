package guru.qa.niffler.service.gql;

import com.apollographql.java.client.ApolloClient;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.UpdateCategoryMutation;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.data.entity.currency.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.type.CategoryInput;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class StatGqlClient {

    private final ApolloClient apolloClient;

    public StatGqlClient(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    private static final Map<CurrencyValues, Double> CURRENCY_RATES = Map.of(
            CurrencyValues.USD, 66.66667,
            CurrencyValues.EUR, 72.0,
            CurrencyValues.RUB, 1.0
    );

    public StatQuery.Stat getStatistic(String bearerToken) {
        return Rx2Apollo.single(apolloClient.query(StatQuery.builder().build())
                        .addHttpHeader("authorization", bearerToken))
                .blockingGet()
                .dataOrThrow()
                .stat;
    }

    public List<SpendJson> getExpectedSpends(UserJson user) {
        return user.testData().spendings().stream()
                .map(spend -> new SpendJson(
                        null,
                        spend.spendDate(),
                        spend.category(),
                        CurrencyValues.RUB,
                        roundToTwoDecimals(spend.amount() * CURRENCY_RATES.get(spend.currency())),
                        "",
                        spend.username()
                ))
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();
    }

    public List<SpendJson> getActualSpends(StatQuery.Stat result, UserJson user) {
        return result.statByCategories.stream()
                .map(stat -> new SpendJson(
                        null,
                        stat.firstSpendDate,
                        new CategoryJson(null, stat.categoryName, user.username(), false),
                        CurrencyValues.valueOf(stat.currency.rawValue),
                        roundToTwoDecimals(stat.sum),
                        "",
                        user.username()
                ))
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();
    }

    public double calculateExpectedTotal(UserJson user) {
        return user.testData().spendings().stream()
                .mapToDouble(spend -> spend.amount() * CURRENCY_RATES.get(spend.currency()))
                .sum();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void archiveCategory(CategoryJson categoryToArchive, String bearerToken){
        Rx2Apollo.single(apolloClient.mutation(UpdateCategoryMutation.builder()
                                .input(CategoryInput.builder()
                                        .id(categoryToArchive.id().toString())
                                        .name(categoryToArchive.name())
                                        .archived(true)
                                        .build())
                                .build())
                        .addHttpHeader("authorization", bearerToken))
                .blockingGet();
    }


    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
