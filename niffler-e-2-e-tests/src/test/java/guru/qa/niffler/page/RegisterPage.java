package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitButton = $("[class='form__submit']");
    private final SelenideElement usernameError = $x("//input[@id='username']/..");
    private final SelenideElement passwordError = $x("//input[@id='password']/..");
    private final SelenideElement passwordSubmitError = $x("//input[@id='passwordSubmit']/..");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    public SuccessRegisterPage submitRegistration() {
        submitButton.click();
        return new SuccessRegisterPage();
    }

    public RegisterPage shouldUsernameError() {
        usernameError.shouldHave(text("Allowed username length should be from 3 to 50 characters"));
        return this;
    }

    public RegisterPage shouldPasswordError() {
        passwordError.shouldHave(text("Allowed password length should be from 3 to 12 characters"));
        return this;
    }

    public void shouldPasswordSubmitError() {
        passwordSubmitError.shouldHave(text("Allowed password length should be from 3 to 12 characters"));
    }
}
