package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement successRegisterMessage = $(".form__paragraph_success");
    private final SelenideElement signInBtn = $(".form_sign-in");
    private final SelenideElement validationErrorUserExists = $x("//span[contains(text(), 'already exists')]");
    private final SelenideElement validationErrorPasswordsShouldBeEqual = $x("//span[contains(text(), 'Passwords should be equal')]");

    @Nonnull
    public RegisterPage setUsername(String username) {
        usernameInput
                .shouldBe(visible)
                .setValue(username);
        return this;
    }

    @Nonnull
    public RegisterPage setPassword(String password) {
        passwordInput
                .shouldBe(visible)
                .setValue(password);
        return this;
    }

    @Nonnull
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput
                .setValue(passwordSubmit);
        return this;
    }

    @Nonnull
    public RegisterPage submitRegistration() {
        submitBtn
                .click();
        return new RegisterPage();
    }

    @Nonnull
    public RegisterPage checkSuccessRegisterMessage(String expectedMessage) {
        successRegisterMessage
                .shouldHave(text(expectedMessage));
        return new RegisterPage();
    }

    @Nonnull
    public LoginPage clickSignInBtn() {
        signInBtn
                .click();
        return new LoginPage();
    }

    public RegisterPage doRegister(String username, String password) {
        setUsername(username);
        setPassword(password);
        setPasswordSubmit(password);
        submitRegistration();
        return new RegisterPage();
    }

    public void checkValidationErrorUserFieldIsDisplayed(String username) {
        validationErrorUserExists
                .shouldHave(text("Username `" + username + "` already exists"));
    }

    public void checkValidationErrorPasswordFieldIsDisplayed() {
        validationErrorPasswordsShouldBeEqual.shouldBe(visible);
    }
}
