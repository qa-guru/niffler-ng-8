package guru.qa.niffler.test.web;

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


    @Test
    void test1() {
        UserDbClient db = new UserDbClient();

        UserJson user = db.createUser(
                new UserJson(
                        UUID.randomUUID(),
                        "Михаил2",
                        "Зубенко2",
                        "Петрович2",
                        "Михаил Зубенко Петрович1",
                        CurrencyValues.RUB,
                        null,
                        null,
                        "5555"
                )
        );
        System.out.println(user);
    }
}
