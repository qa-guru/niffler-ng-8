package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import com.apollographql.apollo.api.Error;
import guru.qa.FriendsWithCategoriesQuery;
import guru.qa.NestedFriends2LevelQuery;
import guru.qa.NestedFriends3LevelQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserGqlTest extends BaseGraphQlTest {

    @User(friends = 1)
    @Test
    @ApiLogin
    void categoriesForFriendsShouldReturnError(@Token String bearerToken) {
        final ApolloCall<FriendsWithCategoriesQuery.Data> friendsWithCategoriesCall =
                apolloClient.query(FriendsWithCategoriesQuery.builder()
                                .page(0)
                                .size(10)
                                .sort(null)
                                .build())
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<FriendsWithCategoriesQuery.Data> response =
                Rx2Apollo.single(friendsWithCategoriesCall).blockingGet();

        assert response.errors != null;
        Error error = response.errors.getFirst();


        Assertions.assertEquals(
                "Can`t query categories for another user",
                error.getMessage()
        );
    }

    @User(friends = 1)
    @Test
    @ApiLogin
    void nestedFriendsShouldReturnError(@Token String bearerToken) {
        final ApolloCall<NestedFriends3LevelQuery.Data> nestedFriendsCall =
                apolloClient.query(NestedFriends3LevelQuery.builder()
                                .page(0)
                                .size(10)
                                .build())
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<NestedFriends3LevelQuery.Data> response =
                Rx2Apollo.single(nestedFriendsCall).blockingGet();
        assert response.errors != null;
        Error error = response.errors.getFirst();


        Assertions.assertEquals(
                "Can`t fetch over 2 friends sub-queries",
                error.getMessage()
        );
    }

    @User(friends = 1)
    @Test
    @ApiLogin
    void nestedFriendsShouldBeForbidden(@Token String bearerToken) {
        final ApolloCall<NestedFriends2LevelQuery.Data> nestedFriendsCall =
                apolloClient.query(NestedFriends2LevelQuery.builder()
                                .page(0)
                                .size(10)
                                .build()
                        )
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<NestedFriends2LevelQuery.Data> response =
                Rx2Apollo.single(nestedFriendsCall).blockingGet();
        assert response.errors != null;
        Error error = response.errors.getFirst();

        Assertions.assertEquals(
                "Nested friends queries are forbidden",
                error.getMessage()
        );
    }
}
