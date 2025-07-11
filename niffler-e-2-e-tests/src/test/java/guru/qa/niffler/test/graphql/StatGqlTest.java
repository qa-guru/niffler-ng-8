package guru.qa.niffler.test.graphql;

import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.data.entity.currency.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.gql.StatGqlClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatGqlTest extends BaseGraphQlTest {

    private final StatGqlClient statGqlClient = new StatGqlClient(apolloClient);

    @Test
    @ApiLogin
    @User
    void allStatShouldBeReturnedFromGateway(@Token String bearerToken) {
        StatQuery.Stat result = statGqlClient.getStatistic(bearerToken);
        assertEquals(0.0, result.total);
    }

    @Test
    @ApiLogin
    @User(spendings = {
            @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 1000,
                    currency = CurrencyValues.USD),
            @Spending(
                    category = "Магазины",
                    description = "Продукты",
                    amount = 100,
                    currency = CurrencyValues.EUR)
    })
    void checkStatWithDifferentCurrencies(@Token String bearerToken, UserJson user) {
        StatQuery.Stat result = statGqlClient.getStatistic(bearerToken);

        List<SpendJson> expectedSpends = statGqlClient.getExpectedSpends(user);
        List<SpendJson> actualSpends = statGqlClient.getActualSpends(result, user);


        assertThat(actualSpends)
                .usingRecursiveComparison()
                .ignoringFields("category.id", "spendDate")
                .isEqualTo(expectedSpends);

        assertEquals(statGqlClient.calculateExpectedTotal(user), result.total, 0.001);
    }

    @Test
    @ApiLogin
    @User(spendings = {
            @Spending(
                    category = "Clothes",
                    description = "jeans",
                    amount = 200,
                    currency = CurrencyValues.USD),
            @Spending(
                    category = "Sports",
                    description = "bike",
                    amount = 2000,
                    currency = CurrencyValues.EUR)
    })
    void checkStatWithArchivedCategory(@Token String bearerToken, UserJson user) {
        CategoryJson categoryToArchive = user.testData().spendings().getFirst().category();

        statGqlClient.archiveCategory(categoryToArchive,bearerToken);

        StatQuery.Stat result = statGqlClient.getStatistic(bearerToken);

        List<SpendJson> expectedSpends = statGqlClient.getExpectedSpends(user);
        List<SpendJson> actualSpends = statGqlClient.getActualSpends(result, user);

        assertThat(actualSpends)
                .usingRecursiveComparison()
                .ignoringFields("category.id", "spendDate", "category.name")
                .isEqualTo(expectedSpends);

        assertEquals("Archived", actualSpends.getFirst().category().name());
        assertEquals(statGqlClient.calculateExpectedTotal(user), result.total, 0.01);
    }
}