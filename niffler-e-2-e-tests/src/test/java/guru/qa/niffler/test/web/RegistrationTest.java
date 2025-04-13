package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest extends BaseTest {


    @Test
    void shouldRegisterNewUser() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccount()
                .doRegister(username, password)
                .checkSuccessRegisterMessage("Congratulations! You've registered!")
                .clickSignInBtn()
                .checkLoginPageShouldBeLoaded();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String existingUser = "test";

        Selenide.open(CFG.authUrl(), RegisterPage.class)
                .doRegister(existingUser, password)
                .checkValidationErrorUserFieldIsDisplayed(existingUser);
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String notEqualPassword = randomPassword();

        Selenide.open(CFG.authUrl(), RegisterPage.class)
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(notEqualPassword)
                .submitRegistration()
                .checkValidationErrorPasswordFieldIsDisplayed();
    }
}
