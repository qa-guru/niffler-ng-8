package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.ElementType;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class RegistrationPage extends BasePage {
    private final SelenideElement loginHyperLink = $(byText("Log in!"));
    private final SelenideElement usernameInput = $("input[id=username]");
    private final SelenideElement passwordInput = $("input[id=password]");
    private final SelenideElement showPasswordIcon = $("button[id=passwordBtn]");
    private final SelenideElement passwordSubmitInput = $("input[id=passwordSubmit]");
    private final SelenideElement showPasswordSubmitIcon = $("button[id=passwordSubmitBtn]");
    private final SelenideElement signUpButton = $(byXpath("//button[normalize-space(text())='Sign Up']"));
    private final SelenideElement signInButton = $(byText("Sign in"));

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
        return new LoginPage();
    }

    public LoginPage redirectToLogin(){
        loginHyperLink.click();
        return new LoginPage();
    }

    public RegistrationPage assertUserExistError(String username){
        String text = String.format("Username `%s` already exists",username);
        $(byText(text)).shouldBe(Condition.visible,Duration.ofSeconds(5));
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
