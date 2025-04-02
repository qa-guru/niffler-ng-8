package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement singInBtn = $(byText("Sign in"));
    private final SelenideElement userExistErr = $(".form__error");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    public LoginPage submitRegistration() {
        submitBtn.click();
        singInBtn.click();
        return new LoginPage();
    }

    public void submitExistingUser(String username) {
        submitBtn.click();
        userExistErr.shouldHave(text("Username `" + username + "` already exists"));
    }

    public void assertEqualPasswordError() {
        submitBtn.click();
        userExistErr.shouldHave(text("Passwords should be equal"));
    }

    public LoginPage doRegister(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(password);
        submitBtn.click();
        singInBtn.click();
        return new LoginPage();
    }
}
