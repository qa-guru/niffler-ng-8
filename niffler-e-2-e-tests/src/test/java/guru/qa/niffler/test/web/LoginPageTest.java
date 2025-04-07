package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.genPassword;

public class LoginPageTest extends BaseWebTest {

    @Test
    @User
    void mainPageShouldBeDisplayedAfterSuccessLogin(WebUser user) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .checkMainPage();
    }

    @Test
    @User
    void userShouldStayOnLoginPageAfterLoginWithBadCredential(WebUser user) {
        String errPassword = genPassword();
        openLoginPage()
                .doLoginError(user.username(), errPassword)
                .checkBadCredentialsError();
    }

}
