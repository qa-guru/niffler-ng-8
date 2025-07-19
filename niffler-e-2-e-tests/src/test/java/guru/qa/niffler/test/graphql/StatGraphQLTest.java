package guru.qa.niffler.test.graphql;

import guru.qa.StatQuery;
import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatGraphQLTest extends BaseGraphQLTest {

    @Test
    @User
    @ApiLogin
    public void allCurrenciesShouldBeReturnedFromGateway() {
        StatQuery.Data responseData = executeSuccessQuery(
            StatQuery.builder().build()
        );

        StatQuery.Stat stat = responseData.stat;
        Assertions.assertEquals(0.0, stat.total);
    }

    @User(
        categories = {
            @Category(name = "Техника", archived = false),
            @Category(name = "Хобби", archived = false)
        },
        spendings = {
            @Spending(
                category = "Техника",
                description = "i9-14900K",
                amount = 71000,
                currency = CurrencyValues.RUB
            ),
            @Spending(
                category = "Хобби",
                description = "Радиоуправляемая модель",
                amount = 1000,
                currency = CurrencyValues.USD
            )
        }
    )
    @ApiLogin
    @Test
    void statByDifferentCurrenciesShouldBeConverted() {
        StatQuery.Data responseData = executeSuccessQuery(
            StatQuery.builder().statCurrency(guru.qa.type.CurrencyValues.EUR).build()
        );

        StatQuery.Stat stat = responseData.stat;

        Assertions.assertAll(
            () -> Assertions.assertEquals(1912.04, stat.total),
            () -> Assertions.assertEquals(CurrencyValues.EUR.name(), stat.currency.rawValue),
            () -> Assertions.assertNotNull(stat.statByCategories),
            () -> Assertions.assertEquals(2, stat.statByCategories.size())
        );
    }

    @User(
        categories = {
            @Category(name = "Техника", archived = true)
        },
        spendings = {
            @Spending(
                category = "Техника",
                description = "i9-14900K",
                amount = 71000,
                currency = CurrencyValues.RUB
            )
        }
    )
    @ApiLogin
    @Test
    void archivedCategoryMustBeRenamed() {
        StatQuery.Data responseData = executeSuccessQuery(
            new StatQuery(null, null, null)
        );

        StatQuery.Stat stat = responseData.stat;

        Assertions.assertAll(
            () -> Assertions.assertEquals(71000.0, stat.total),
            () -> Assertions.assertEquals(CurrencyValues.RUB.name(), stat.currency.rawValue),
            () -> Assertions.assertNotNull(stat.statByCategories),
            () -> Assertions.assertEquals(1, stat.statByCategories.size()),
            () -> Assertions.assertEquals("Archived", stat.statByCategories.getFirst().categoryName)
        );
    }
}
