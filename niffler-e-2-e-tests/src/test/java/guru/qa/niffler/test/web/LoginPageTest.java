package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.genPassword;

public class LoginPageTest extends BaseWebTest {

    @Test
    @User(username = "user")
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserParts user) {
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
