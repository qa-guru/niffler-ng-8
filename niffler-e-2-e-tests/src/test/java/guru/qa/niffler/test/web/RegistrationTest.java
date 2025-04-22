package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.Utils.RandomDataUtils;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private final RandomDataUtils dataUtils = new RandomDataUtils();
    private final String username = dataUtils.randomUserName();
    private final String password = dataUtils.randomUserPassword();

    @Test
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .doRegister(username, password)
                .doLogin(username, password)
                .checkLoadingMainPage();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .doRegister(username, password);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitExistingUser(username);
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit("12345")
                .assertEqualPasswordError();
    }
}
