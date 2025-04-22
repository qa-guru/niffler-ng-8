package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.Utils.RandomDataUtils;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

    private static final Config CFG = Config.getInstance();
    private RandomDataUtils dataUtils = new RandomDataUtils();
    private String username = dataUtils.randomUserName();
    private String password = dataUtils.randomUserPassword();
    private String uncorrectPassword = "12346";

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .doRegister(username, password)
                .doLogin(username, password)
                .checkLoadingMainPage();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterNewAccount()
                .doRegister(username, password)
                .credentialsError(username, uncorrectPassword);
    }
}
