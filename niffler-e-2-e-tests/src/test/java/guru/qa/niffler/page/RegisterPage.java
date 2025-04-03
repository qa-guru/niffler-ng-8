package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage {

    private final SelenideElement usernameInput = $x("//input[@id='username']");
    private final SelenideElement passwordInput = $x("//input[@id='password']");
    private final SelenideElement passwordSubmitInput = $x("//input[@id='passwordSubmit']");
    private final SelenideElement signUpButton = $x("//button[@type='submit']");

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

    public RegisterPage submitRegistration() {
        signUpButton.click();
        return this;
    }

    public SuccessfulRegistrationPage doRegister(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(password);
        signUpButton.click();
        return new SuccessfulRegistrationPage();
    }
}
