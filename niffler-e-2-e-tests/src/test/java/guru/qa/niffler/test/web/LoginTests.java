package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginTests {
    private static final Config CFG = Config.getInstance();
    private static final Faker faker = new Faker();

    @Test
    @DisplayName("Авторизация с валидными данными")
    void shouldAuthorizationValidData() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin("ilesnikov", "12345")
            .shouldStatisticsVisible()
            .shouldHistoryOfSpendingVisible();
    }

    @Test
    @DisplayName("Авторизация с не валидными данными")
    void shouldAuthorizationNotValidData() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(faker.name().username(), faker.internet().password());

        new LoginPage().shouldErrorVisible();
    }
}
