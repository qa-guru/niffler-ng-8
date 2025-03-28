package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.PasswordType;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationPage extends BasePage {
    private static SelenideElement loginHyperLink = $(byText("Log in!"));
    private static SelenideElement usernameInput = $("input[id=username]");
    private static SelenideElement passwordInput = $("input[id=password]");
    private static SelenideElement showPasswordIcon = $("button[id=passwordBtn]");
    private static SelenideElement passwordSubmitInput = $("input[id=passwordSubmit]");
    private static SelenideElement showPasswordSubmitIcon = $("button[id=passwordSubmitBtn]");
    private static SelenideElement signUpButton = $(byXpath("//button[normalize-space(text())='Sign Up']"));
    private static SelenideElement signInButton = $(byText("Sign in"));

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

    public RegistrationPage assertPasswordType(PasswordType type){
        assertEquals(type.toString(),passwordInput.getAttribute("type"));
        return this;
    }
    public RegistrationPage assertPasswordSubmitType(PasswordType type){
        assertEquals(type.toString(),passwordSubmitInput.getAttribute("type"));
        return this;
    }
}
