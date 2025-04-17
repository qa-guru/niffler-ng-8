package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.DataUtils;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

    private static final Config CFG = Config.getInstance();
    private DataUtils dataUtils = new DataUtils();
    private String username = dataUtils.generateUserName();
    private String password = dataUtils.generateUserPassword();
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
