package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegistrationTests {
    private static final Config CFG = Config.getInstance();
    private final Faker faker = new Faker();
    private final String passwordValid = faker.internet().password(3, 12);
    private final String passwordNonValid = faker.internet().password();

    @Test
    @DisplayName("Регистрация нового пользователя")
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .registerSubmit()
                .setUsername(faker.name().username())
                .setPassword(passwordValid)
                .setPasswordSubmit(passwordValid)
                .submitRegistration()
                .shouldRegistrationText();
    }

    @Test
    @DisplayName("Регистрация нового пользователя с невалидным паролем")
    void shouldRegisteringNewUserInvalidPassword() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .registerSubmit()
                .setUsername(faker.name().username())
                .setPassword(passwordNonValid)
                .setPasswordSubmit(passwordNonValid)
                .submitRegistration();
    }
}
