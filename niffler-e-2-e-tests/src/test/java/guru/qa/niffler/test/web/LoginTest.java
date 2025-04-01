package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.BasePage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginTest {

    private static final Config CFG = Config.getInstance();
    private static final Faker faker = new Faker();

    @Test
    @DisplayName("Проверить, что при вводе некорректных username+password пользователь остается на странице логина")
    void shouldStayOnLoginPageAfterLoginWithBAdCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(faker.name().username(), faker.internet().password(3, 12))
                .assertErrorShown("Неверные учетные данные пользователя");
    }}
