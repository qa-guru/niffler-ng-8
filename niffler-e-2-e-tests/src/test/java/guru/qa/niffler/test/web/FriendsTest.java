package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.enums.CurrencyValues;
import guru.qa.niffler.model.users.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class FriendsTest {


    private static final Config CFG = Config.getInstance();

    private UserJson generateUser() {
        Faker faker = new Faker();
        return new UserJson(
                UUID.randomUUID(),
                faker.name().username(),
                faker.name().firstName(),
                faker.name().lastName(),
                faker.name().fullName(),
                CurrencyValues.RUB,
                null,
                null,
                "5555"
        );
    }

    @Test
    void addFriendInvitationTest() {
        UserDbClient db = new UserDbClient();
        UserJson user1 = db.createUserTxChainedJdbc(generateUser());
        UserJson user2 = db.createUserTxChainedJdbc(generateUser());
        db.addIncomeInvitation(user1.id(), user2.id());
        db.deleteUser(user1);
        db.deleteUser(user2);
    }


    @Test
    void addFriendOutTest() {
        UserDbClient db = new UserDbClient();
        UserJson user1 = db.createUserTxChainedJdbc(generateUser());
        UserJson user2 = db.createUserTxChainedJdbc(generateUser());
        db.addOutcomeInvitation(user1.id(), user2.id());
    }


    @Test
    void addFriendTest() {
        UserDbClient db = new UserDbClient();
        UserJson user1 = db.createUserTxChainedJdbc(generateUser());
        UserJson user2 = db.createUserTxChainedJdbc(generateUser());
        db.addFriend(user1.id(), user2.id());
    }
}
