package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement createNewAccountBtn = $(".form__register");
    private final SelenideElement badCredentialsValidationError = $(".form__error");

    public MainPage doLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitBtn
                .click();
        return new MainPage();
    }

    public RegisterPage clickCreateNewAccount() {
        createNewAccountBtn
                .click();
        return new RegisterPage();
    }

    public void checkLoginPageShouldBeLoaded() {
        $("h1").shouldHave(text("Log in"));
    }

    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public LoginPage clickSubmitBtn() {
        submitBtn
                .click();
        return this;
    }

    public void checkBadCredentialsValidationErrorIsDisplayed(String errorMessage) {
        badCredentialsValidationError.shouldHave(text(errorMessage));
    }
}
