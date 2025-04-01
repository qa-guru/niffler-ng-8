package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.UseUser;
import guru.qa.niffler.web.model.User;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.GenerationUtil.genPassword;
import static guru.qa.niffler.util.GenerationUtil.genUsername;

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
    @UseUser
    void shouldNotRegisterUserWithExitingUsername(User user) {
        String username = user.username();
        String password2 = genPassword();
        openRegisterPage()
                .registerUserError(username, password2, password2)
                .checkUsernameError("Username `%s` already exists".formatted(username));
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = genUsername();
        String password1 = genPassword();
        String password2 = genPassword();
        openRegisterPage()
                .registerUserError(username, password1, password2)
                .checkPasswordError("Passwords should be equal");
    }

}
