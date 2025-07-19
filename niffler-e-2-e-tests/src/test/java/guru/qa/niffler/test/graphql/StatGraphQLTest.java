package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatGraphQLTest extends BaseGraphQLTest {

    @Test
    @User
    @ApiLogin
    public void allCurrenciesShouldBeReturnedFromGateway(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> statisticCall = apolloClient.query(StatQuery.builder()
                .build()
            )
            .addHttpHeader("authorization", bearerToken);
        ApolloResponse<StatQuery.Data> dataApolloResponse = Rx2Apollo.single(statisticCall).blockingGet();
        StatQuery.Data data = dataApolloResponse.dataOrThrow();
        StatQuery.Stat stat = data.stat;
        Assertions.assertEquals(0.0, stat.total);
    }
}
