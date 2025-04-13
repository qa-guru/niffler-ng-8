package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;

@ExtendWith(BrowserExtension.class)
public class LoginTest extends BaseTest {

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(CFG.authUrl(), RegisterPage.class)
                .doRegister(username, password)
                .clickSignInBtn()
                .doLogin(username, password)
                .checkMainPageShouldBeLoaded();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String wrongPassword = randomPassword();
        Selenide.open(CFG.authUrl(), RegisterPage.class)
                .doRegister(username, password)
                .clickSignInBtn()
                .setUsername(username)
                .setPassword(wrongPassword)
                .clickSubmitBtn()
                .checkBadCredentialsValidationErrorIsDisplayed("Bad credentials");
    }
}
