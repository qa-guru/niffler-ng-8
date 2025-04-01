package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private static final Faker faker = new Faker();
    private String username;
    private String password;

    @BeforeEach
    void setUp() {
        username = faker.name().username();
        password = faker.internet().password(3, 12);
    }

    @Test
    @DisplayName("Проверить успешную регистрацию")
    void testSuccessRegistration() {
        Selenide.open(CFG.registrationUrl(), RegisterPage.class)
                .submitRegistration(username, password)
                .goToLoginAfterSuccessfulRegistration()
                .doLogin(username, password)
                .assertMainPageOpened();
    }

    @Test
    @DisplayName("Проверить, что регистрация с существующим username невозможна")
    void shouldNotRegisterUserWithExistingUsername() {
        var existingUser = "user1";
        Selenide.open(CFG.registrationUrl(), RegisterPage.class)
                .submitRegistration(existingUser, password)
                .assertErrorShown("Username `" + existingUser + "` already exists");
    }

    @Test
    @DisplayName("Проверить, что если пароль и подтверждение пароля отличаются - регистрация невозможна")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        Selenide.open(CFG.registrationUrl(), RegisterPage.class)
                .submitRegistrationWithDifferentPasswords(username, password, password + "1")
                .assertErrorShown("Passwords should be equal");
    }

}