package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.ElementType;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;

@ParametersAreNonnullByDefault
public class RegistrationPage extends BasePage {

    private final SelenideElement loginHyperLink;
    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement showPasswordIcon;
    private final SelenideElement passwordSubmitInput;
    private final SelenideElement showPasswordSubmitIcon;
    private final SelenideElement signUpButton;
    private final SelenideElement signInButton;

    public RegistrationPage(@Nullable SelenideDriver driver) {
        super(driver);
        this.loginHyperLink = $(byText("Log in!"));
        this.usernameInput = $("input[id=username]");
        this.passwordInput = $("input[id=password]");
        this.showPasswordIcon = $("button[id=passwordBtn]");
        this.passwordSubmitInput = $("input[id=passwordSubmit]");
        this.showPasswordSubmitIcon = $("button[id=passwordSubmitBtn]");
        this.signUpButton = $(byXpath("//button[normalize-space(text())='Sign Up']"));
        this.signInButton = $(byText("Sign in"));
    }

    public RegistrationPage() {
        this(null);
    }

    @Step("Set username {userName}")
    public RegistrationPage setUserName(String userName) {
        usernameInput.val(userName);
        return this;
    }

    @Step("Set password {password}")
    public RegistrationPage setPassword(String password) {
        passwordInput.val(password);
        return this;
    }

    @Step("Assert password is {password}")
    public RegistrationPage assertPassword(String password) {
        Assertions.assertEquals(passwordInput.val(),password);
        return this;
    }

    @Step("assert password submit is {password}")
    public RegistrationPage assertPasswordSubmit(String password) {
        Assertions.assertEquals(passwordSubmitInput.val(),password);
        return this;
    }

    @Step("Set password submit {password}")
    public RegistrationPage setPasswordSubmit(String password) {
        passwordSubmitInput.val(password);
        return this;
    }

    @Step("Click show password icon")
    public RegistrationPage showPassword() {
        showPasswordIcon.click();
        return this;
    }

    @Step("Click show password submit icon")
    public RegistrationPage showPasswordSubmit() {
        showPasswordSubmitIcon.click();
        return this;
    }

    @Step("Click sign up button")
    public RegistrationPage clickSignUp() {
        signUpButton.click();
        return this;
    }

    @Step("Click sign in button")
    public LoginPage clickSignIn() {
        signInButton
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        return new LoginPage(driver);
    }

    @Step("Click log in hyperlink")
    public LoginPage redirectToLogin(){
        loginHyperLink.click();
        return new LoginPage(driver);
    }

    @Step("Verify error message that username '{username}' already exists")
    public RegistrationPage assertUserExistError(String username) {
        SelenideElement errorNotification = $(byText(String.format("Username `%s` already exists", username)));
        errorNotification.shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify no redirect to Sign In page occurs")
    public RegistrationPage assertNoRedirectToSignInPage() {
        signInButton.shouldNot(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify 'Username cannot be blank' validation error appears")
    public RegistrationPage assertUsernameCannotBlank() {
        $(byText("Username can not be blank")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify 'Password cannot be blank' validation error appears")
    public RegistrationPage assertPasswordCannotBlank() {
        $(byText("Password can not be blank")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify username length validation message appears")
    public RegistrationPage assertUsernameLength() {
        $(byText("Allowed username length should be from 3 to 50 characters")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify password length validation message appears")
    public RegistrationPage assertPasswordLength() {
        $(byText("Allowed password length should be from 3 to 12 characters")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify 'Passwords should be equal' validation message appears")
    public RegistrationPage assertPasswordShouldBeEqual() {
        $(byText("Passwords should be equal")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify password input field has {type} type")
    public RegistrationPage assertPasswordType(ElementType type) {
        passwordInput.shouldHave(type.assertType());
        return this;
    }

    @Step("Verify password confirmation field has {type} type")
    public RegistrationPage assertPasswordSubmitType(ElementType type) {
        passwordSubmitInput.shouldHave(type.assertType());
        return this;
    }
}
