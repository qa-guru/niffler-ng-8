package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.niffler.util.RandomDataUtils.genPassword;

public class LoginPageTest extends BaseWebTest {

    public static final SelenideConfig Sc = new SelenideConfig()
        .browser("chrome")
        .pageLoadStrategy("eager")
        .timeout(5000L);

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();

    @Test
    @User(username = "user")
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserParts user) {
        SelenideDriver selenideDriver = new SelenideDriver(Sc);
        browserExtension.drivers().add(selenideDriver);
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
            .checkMainPage();
    }

    @Test
    @User
    void userShouldStayOnLoginPageAfterLoginWithBadCredential(UserParts user) {
        String errPassword = genPassword();
        openLoginPage()
            .doLoginError(user.getUsername(), errPassword)
            .checkBadCredentialsError();
    }
}
