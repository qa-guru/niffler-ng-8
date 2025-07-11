package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.data.entity.currency.CurrencyValues;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrenciesGqlTest extends BaseGraphQlTest{

    @User
    @ApiLogin
    @Test
    void allCurrenciesShouldBeReturnedFromGateway(@Token String bearerToken){
        ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("authorization","Bearer " + bearerToken);

        ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final CurrenciesQuery.Data data = response.dataOrThrow();
        List<CurrenciesQuery.Currency> all = data.currencies;
        assertEquals(
                CurrencyValues.RUB.name(),
                all.get(0).currency.rawValue
        );
        assertEquals(
                CurrencyValues.KZT.name(),
                all.get(1).currency.rawValue
        );
        assertEquals(
                CurrencyValues.EUR.name(),
                all.get(2).currency.rawValue
        );
        assertEquals(
                CurrencyValues.USD.name(),
                all.get(3).currency.rawValue
        );


    }
}
