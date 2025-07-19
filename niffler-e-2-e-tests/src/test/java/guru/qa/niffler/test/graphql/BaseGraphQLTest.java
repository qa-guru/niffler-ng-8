package guru.qa.niffler.test.graphql;

import com.apollographql.adapter.core.DateAdapter;
import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Query;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.client.ApolloClient;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.GraphQLTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.type.Date;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

@GraphQLTest
public class BaseGraphQLTest {

    @RegisterExtension
    protected static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLOGinExtension();

    protected static final Config CFG = Config.getInstance();

    protected static final ApolloClient apolloClient = new ApolloClient.Builder()
        .serverUrl(CFG.gatewayUrl() + "graphql")
        .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
        .okHttpClient(
            new OkHttpClient.Builder()
                .addNetworkInterceptor(new AllureOkHttp3())
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        ).build();

    protected <T extends Query.Data> ApolloResponse<T> executeQuery(Query<T> query) {
        ApolloCall<T> call = apolloClient.query(query)
            .addHttpHeader("authorization", "Bearer " + ApiLoginExtension.getToken());
        return Rx2Apollo.single(call).blockingGet();
    }

    protected <T extends Query.Data> T executeSuccessQuery(Query<T> query) {
        ApolloResponse<T> response = executeQuery(query);
        return Assertions.assertDoesNotThrow(response::dataOrThrow);
    }

    protected <T extends Query.Data> List<Error> executeErrorQuery(Query<T> query) {
        ApolloResponse<T> response = executeQuery(query);
        Assertions.assertTrue(response.hasErrors());
        return response.errors;
    }
}
