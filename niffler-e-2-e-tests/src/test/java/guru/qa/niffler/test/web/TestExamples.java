package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.users.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class TestExamples {

    private static final Config CFG = Config.getInstance();

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

        UserJson user = db.createUserSpringJdbc(
                new UserJson(
                        UUID.randomUUID(),
                        "Михаил8",
                        "Зубенко8",
                        "Петрович8",
                        "Михаил Зубенко Петрович8",
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

        SpendJson spend = db.createSpringJdbcSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "test-cat-tx4",
                                CFG.mainUserLogin(),
                                false
                        ),
                        CurrencyValues.RUB,
                        100.0,
                        "test desc-tx4",
                        CFG.mainUserLogin()
                )
        );
        System.out.println(spend);
    }
}
