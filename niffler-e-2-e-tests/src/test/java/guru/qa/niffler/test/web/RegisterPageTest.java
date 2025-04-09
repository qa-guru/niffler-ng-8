package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.genPassword;
import static guru.qa.niffler.util.RandomDataUtils.genUsername;

public class RegisterPageTest extends BaseWebTest {

    @Test
    void shouldRegisterNewUser() {
        String username = genUsername();
        String password = genPassword();
        openRegisterPage()
                .registerUserSuccess(username, password, password)
                .checkSuccessfulRegistrationPage();
    }

    @Test
    @User
    void shouldNotRegisterUserWithExitingUsername(WebUser user) {
        String username = user.username();
        String password2 = genPassword();
        openRegisterPage()
                .registerUserError(username, password2, password2)
                .checkUsernameError("Username `%s` already exists".formatted(username));
    }

    @Test
    @DisabledByIssue("3")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = genUsername();
        String password1 = genPassword();
        String password2 = genPassword();
        openRegisterPage()
                .registerUserError(username, password1, password2)
                .checkPasswordError("Passwords should be equal");
    }

}
