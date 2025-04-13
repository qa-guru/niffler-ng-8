package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extention.TextMethodContextExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import static utils.FakerGenUtil.*;

public class LoginTest {

    private static String newUser;
    private static String newPassword;
    private static final Config CFG = Config.getInstance();

    @BeforeAll
    public static void beforeAll() {
        newUser = genRandomName();
        newPassword = genRandomPassword();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doRegister()
                .doRegister(newUser, newPassword);
    }

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        // TODO: добавил extensionContext, чтобы понять, как работает глобальная регистрация
        // Подробнее: https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic
        ExtensionContext extensionContext = TextMethodContextExtension.context();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(newUser, newPassword)
                .checkThatMainPageOpened();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername("1")
                .setPassword("2")
                .submitLogin()
                .checkThatUserIsNotCorrect();
    }

    @Test
    void shouldShowMessageIfUsernameIsNotFilled() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .submitLogin()
                .checkThatUsernameIsNotFilled();
    }
}
