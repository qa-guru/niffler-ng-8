package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegistrationTests {
    private static final Config CFG = Config.getInstance();
    private final Faker faker = new Faker();
    private final String passwordValid = faker.internet().password(3, 12);
    private final String passwordNonValid = faker.internet().password(1, 2);

    @Test
    @DisplayName("Регистрация нового пользователя")
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .registerSubmit()
            .setUsername(RandomDataUtils.randomUsername())
            .setPassword(passwordValid)
            .setPasswordSubmit(passwordValid)
            .submitRegistration()
            .shouldRegistrationText();
    }

    @Test
    @DisplayName("Регистрация нового пользователя с невалидными данными")
    void shouldRegisteringNewUserInvalidPassword() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .registerSubmit()
            .setUsername("a")
            .setPassword(passwordNonValid)
            .setPasswordSubmit(passwordNonValid)
            .submitRegistration();

        new RegisterPage().shouldUsernameError()
            .shouldPasswordError()
            .shouldPasswordSubmitError();
    }
}
