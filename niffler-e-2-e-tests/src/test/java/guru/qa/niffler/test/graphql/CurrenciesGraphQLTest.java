package guru.qa.niffler.test.graphql;

import guru.qa.CurrenciesQuery;
import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CurrenciesGraphQLTest extends BaseGraphQLTest {

    @Test
    @User
    @ApiLogin
    public void allCurrenciesShouldBeReturnedFromGateway() {
        CurrenciesQuery.Data responseData = executeSuccessQuery(new CurrenciesQuery());

        List<CurrenciesQuery.Currency> currencies = responseData.currencies;
        Assertions.assertEquals(CurrencyValues.RUB.getDataValue(), currencies.get(0).currency.rawValue);
        Assertions.assertEquals(CurrencyValues.KZT.getDataValue(), currencies.get(1).currency.rawValue);
        Assertions.assertEquals(CurrencyValues.EUR.getDataValue(), currencies.get(2).currency.rawValue);
        Assertions.assertEquals(CurrencyValues.USD.getDataValue(), currencies.get(3).currency.rawValue);
    }
}
