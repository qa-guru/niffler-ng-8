package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static utils.FakerGenUtil.genRandomName;
import static utils.FakerGenUtil.genRandomPassword;


public class RegisterTest {

    private static String newUser;
    private static String newPassword;

    @BeforeAll
    public static void beforeAll() {

        newUser = genRandomName();
        newPassword = genRandomPassword();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doRegister()
                .doRegister(newUser, newPassword);

    }

    @AfterAll
    public static void afterAll() {
        Selenide.closeWebDriver();
    }

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .doRegister(genRandomName(), genRandomPassword())
                .checkMessageOfSuccessfulRegistration("Congratulations! You've registered!")
                .signIn()
                .doLogin(newUser, newPassword)
                .checkThatMainPageOpened();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .setUsername(newUser)
                .setPassword(newPassword)
                .setPasswordSubmit(newPassword)
                .submitRegistration()
                .checkUserNameIsAlreadyExist();
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {

        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .setUsername(genRandomName())
                .setPassword(genRandomPassword())
                .setPasswordSubmit(genRandomPassword())
                .submitRegistration()
                .checkPasswordsAreNotEquals();
    }

    @Test
    void shouldShowErrorIfUsernameMinimalLength() {

        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .setUsername("a")
                .setPassword(newPassword)
                .setPasswordSubmit(newPassword)
                .submitRegistration()
                .checkUserNameLengthIncorrect();
    }

    @Test
    void shouldShowErrorIfPasswordMaxLength() {
        String password = genRandomPassword() + RandomStringUtils.randomAlphanumeric(30);
        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .setUsername(genRandomName())
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkPasswordLengthIncorrect();
    }
}
