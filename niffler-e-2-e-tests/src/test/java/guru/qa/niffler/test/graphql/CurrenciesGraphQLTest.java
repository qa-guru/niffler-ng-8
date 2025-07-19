package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CurrenciesGraphQLTest extends BaseGraphQLTest {

    @Test
    @User
    @ApiLogin
    public void allCurrenciesShouldBeReturnedFromGateway(@Token String bearerToken) {
        ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
            .addHttpHeader("authorization", bearerToken);
        ApolloResponse<CurrenciesQuery.Data> dataApolloResponse = Rx2Apollo.single(currenciesCall).blockingGet();
        CurrenciesQuery.Data data = dataApolloResponse.dataOrThrow();
        List<CurrenciesQuery.Currency> currencies = data.currencies;
        Assertions.assertEquals(CurrencyValues.RUB.getDataValue(), currencies.get(0).currency.rawValue);
        Assertions.assertEquals(CurrencyValues.KZT.getDataValue(), currencies.get(1).currency.rawValue);
        Assertions.assertEquals(CurrencyValues.EUR.getDataValue(), currencies.get(2).currency.rawValue);
        Assertions.assertEquals(CurrencyValues.USD.getDataValue(), currencies.get(3).currency.rawValue);
    }
}
