package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.db.Realization;
import guru.qa.niffler.service.db.SpendDbClient;
import guru.qa.niffler.service.db.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;


public class JdbcTest {

    private static final String PASSWORD = "12345";

    @ParameterizedTest
    @EnumSource(Realization.class)
    void createUserWithFriendTest(Realization realization) {
        UsersDbClient userClient = new UsersDbClient(realization);
        UserJson user = userClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        userClient.createFriends(user,1);
        System.out.println(user);
    }

    @ParameterizedTest
    @EnumSource(Realization.class)
    void createUserWithOutcomeRequestTest(Realization realization) {
        UsersDbClient userClient = new UsersDbClient(realization);
        UserJson requester = userClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        userClient.createOutcomeInvitations(requester, 1);
        System.out.println(requester);
    }

    @ParameterizedTest
    @EnumSource(Realization.class)
    void createUserWithIncomeRequestTest(Realization realization) {
        UsersDbClient userClient = new UsersDbClient(realization);
        UserJson requester = userClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        userClient.createIncomeInvitations(requester, 1);
        System.out.println(requester);
    }

    @ParameterizedTest
    @EnumSource(Realization.class)
    void spendRepositoryJdbcTest(Realization realization) {
        UserJson user = new UsersDbClient(realization).createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );

        SpendDbClient spendDbClient = new SpendDbClient(realization);

        SpendJson spend = spendDbClient.createSpend(SpendJson.spendJson(
                user.username(),
                RandomDataUtils.randomCategoryName()

        ));

        Optional<SpendJson> spendById = spendDbClient.findById(spend.id());
        Assertions.assertTrue(spendById.isPresent());
        System.out.println(spendById);
    }

}