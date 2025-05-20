package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.ElementType;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;

public class RegistrationPage extends BasePage {

    private final SelenideElement loginHyperLink;
    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement showPasswordIcon;
    private final SelenideElement passwordSubmitInput;
    private final SelenideElement showPasswordSubmitIcon;
    private final SelenideElement signUpButton;
    private final SelenideElement signInButton;

    public RegistrationPage(SelenideDriver driver) {
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

    public RegistrationPage setUserName(String userName) {
        usernameInput.val(userName);
        return this;
    }
    public RegistrationPage setPassword(String password) {
        passwordInput.val(password);
        return this;
    }

    public RegistrationPage assertPassword(String password) {
        Assertions.assertEquals(passwordInput.val(),password);
        return this;
    }

    public RegistrationPage assertPasswordSubmit(String password) {
        Assertions.assertEquals(passwordSubmitInput.val(),password);
        return this;
    }

    public RegistrationPage setPasswordSubmit(String password) {
        passwordSubmitInput.val(password);
        return this;
    }

    public RegistrationPage showPassword() {
        showPasswordIcon.click();
        return this;
    }

    public RegistrationPage showPasswordSubmit() {
        showPasswordSubmitIcon.click();
        return this;
    }

    public RegistrationPage clickSignUp() {
        signUpButton.click();
        return this;
    }
    public LoginPage clickSignIn() {
        signInButton
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        return new LoginPage(driver);
    }

    public LoginPage redirectToLogin(){
        loginHyperLink.click();
        return new LoginPage(driver);
    }

    public RegistrationPage assertUserExistError(String username){
        SelenideElement errorNotification = $(byText(String.format("Username `%s` already exists",username)));
        errorNotification.shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public RegistrationPage assertNoRedirectToSignInPage(){
        signInButton.shouldNot(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public RegistrationPage assertUsernameCannotBlank(){
        $(byText("Username can not be blank")).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }
    public RegistrationPage assertPasswordCannotBlank(){
        $(byText("Password can not be blank")).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public RegistrationPage assertUsernameLength(){
        $(byText("Allowed username length should be from 3 to 50 characters")).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public RegistrationPage assertPasswordLength(){
        $(byText("Allowed password length should be from 3 to 12 characters")).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public RegistrationPage assertPasswordShouldBeEqual(){
        $(byText("Passwords should be equal")).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public RegistrationPage assertPasswordType(ElementType type){
        passwordInput.shouldHave(type.assertType());
        return this;
    }
    public RegistrationPage assertPasswordSubmitType(ElementType type){
        passwordSubmitInput.shouldHave(type.assertType());
        return this;
    }
}
