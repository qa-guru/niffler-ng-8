package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Browser;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.StaticBrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;

@ExtendWith(BrowserExtension.class)
public class LoginTest extends BaseTest {

    @RegisterExtension
    private static final StaticBrowserExtension BROWSER_EXTENSION = new StaticBrowserExtension();

    @ParameterizedTest
    @EnumSource(Browser.class)
    void mainPageShouldBeDisplayedAfterSuccessLogin(Browser browser) {

        SelenideDriver driver = new SelenideDriver(browser.config());

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .clickCreateNewAccount()
                .doRegister(username, password)
                .clickSignInBtn()
                .doLogin(username, password)
                .checkMainPageShouldBeLoaded();
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {

        BROWSER_EXTENSION.drivers().add(driver);
        String wrongPassword = randomPassword();

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .clickCreateNewAccount()
                .doRegister(username, password)
                .clickSignInBtn()
                .setUsername(username)
                .setPassword(wrongPassword)
                .clickSubmitBtn()
                .checkBadCredentialsValidationErrorIsDisplayed("Bad credentials");
    }
}
