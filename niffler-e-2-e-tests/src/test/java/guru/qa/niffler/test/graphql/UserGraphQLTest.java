package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.Error;
import guru.qa.CategoriesAnotherUserQuery;
import guru.qa.SubQueriesFriendsOver2Query;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserGraphQLTest extends BaseGraphQLTest {

    @User(withFriend = 1)
    @ApiLogin
    @Test
    void anotherUserCategoriesShouldNotBeReturned() {
        List<Error> errors = executeErrorQuery(
            new CategoriesAnotherUserQuery(0, 1)
        );

        Assertions.assertEquals("Can`t query categories for another user", errors.getFirst().getMessage());
    }

    @User
    @ApiLogin
    @Test
    void subQueriesFriendsOver2ShouldNotBeReturned() {
        List<Error> errors = executeErrorQuery(
            new SubQueriesFriendsOver2Query(0, 1)
        );

        Assertions.assertEquals("Can`t fetch over 2 friends sub-queries", errors.getFirst().getMessage());
    }
}
