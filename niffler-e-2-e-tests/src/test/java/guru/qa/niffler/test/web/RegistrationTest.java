package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private Faker faker = new Faker();
    private String username;
    private String passworg;

    @BeforeEach
    void setUp() {
        username = faker.name().username();
        passworg = faker.internet().password(3, 6);
    }

    @Test
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .doRegister(username, passworg)
                .doLogin(username, passworg)
                .checkLoadingMainPage();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .doRegister(username, passworg);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .setUsername(username)
                .setPassword(passworg)
                .setPasswordSubmit(passworg)
                .submitExistingUser(username);
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .setUsername(username)
                .setPassword(passworg)
                .setPasswordSubmit("12345")
                .assertEqualPasswordError();
    }
}
