package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.UseUser;
import guru.qa.niffler.web.model.User;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.GenerationUtil.genPassword;

public class LoginPageTest extends BaseWebTest {

    @Test
    @UseUser
    void mainPageShouldBeDisplayedAfterSuccessLogin(User user) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .checkMainPage();
    }

    @Test
    @UseUser
    void userShouldStayOnLoginPageAfterLoginWithBadCredential(User user) {
        String errPassword = genPassword();
        openLoginPage()
                .doLoginError(user.username(), errPassword)
                .checkBadCredentialsError();
    }

}
