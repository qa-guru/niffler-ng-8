package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import com.atomikos.util.Assert;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.data.enums.CurrencyValues;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

public class CurrenciesGraphqlTest extends BaseGraphQlTest {


    @ApiLogin(username = "test", password = "12345")
    @Test
    void allCurrenciesShouldBeReturned(@Token String token){
        Assertions.assertNotNull(token);
        open(CFG.frontUrl());
        ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("authorization", token);

        ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();

        final CurrenciesQuery.Data data = response.dataOrThrow();


        CurrenciesQuery.Currency first = data.currencies.get(0);
        Assertions.assertEquals(
                CurrencyValues.RUB.name(),
                first.currency.rawValue
        );
    }
}
