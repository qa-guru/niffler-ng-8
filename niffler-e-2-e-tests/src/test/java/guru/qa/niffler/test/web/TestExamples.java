package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.enums.CurrencyValues;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.users.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class TestExamples {

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
    void test() {
        SpendDbClient db = new SpendDbClient();

        SpendJson spend = db.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-cat-tx1",
                                "test",
                                false
                        ),
                        CurrencyValues.RUB,
                        100.0,
                        "test desc-tx1",
                        null
                )
        );
        System.out.println(spend);
    }


    //Закомментировано, поскольку в уроке 5.1 произошёл переход на Spring Jdbc
    //  и метод AuthAuthorityDao.create стал принимать AuthAuthorityEntity...
//    @Test
//    void test1() {
//        UserDbClient db = new UserDbClient();
//
//        UserJson user = db.createUser(
//                new UserJson(
//                        UUID.randomUUID(),
//                        "Михаил2",
//                        "Зубенко2",
//                        "Петрович2",
//                        "Михаил Зубенко Петрович1",
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        "5555"
//                )
//        );
//        System.out.println(user);
//    }

    @Test
    void springJdbcTest() {
        UserDbClient db = new UserDbClient();

        UserJson user = db.createUserTxChainedJdbc(
                new UserJson(
                        UUID.randomUUID(),
                        "Михаил17",
                        "Зубенко17",
                        "Петрович17",
                        "Михаил Зубенко Петрович17",
                        CurrencyValues.RUB,
                        null,
                        null,
                        "5555"
                )
        );
        System.out.println(user);
    }

    @Test
    void springJdbcSpendTest() {
        SpendDbClient db = new SpendDbClient();

        SpendJson spend = db.createTxSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-cat-tx11",
                                CFG.mainUserLogin(),
                                false
                        ),
                        CurrencyValues.RUB,
                        100.0,
                        "test desc-tx11",
                        CFG.mainUserLogin()
                )
        );
        System.out.println(spend);
    }
}
