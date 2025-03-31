package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage extends BasePage{

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitPasswordInput = $("#passwordSubmit");
    private final SelenideElement signUpBtn = $("button[class='form__submit']");
    private final SelenideElement signInBtn = $("a[class='form_sign-in']");
    private final SelenideElement successRegistrationMessage = $x("//p[contains(text(), 'Congratulations')]");
    private final SelenideElement usernameExistsMessage = $x("//span[contains(text(), 'already exists')]");


    public RegisterPage submitRegistration(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitPasswordInput.setValue(password);
        signUpBtn.click();
        return this;
    }

    public RegisterPage submitRegistrationWithDifferentPasswords(String username, String password, String submitPassword) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitPasswordInput.setValue(submitPassword);
        signUpBtn.click();
        return this;
    }

    public LoginPage goToLoginAfterSuccessfulRegistration() {
        successRegistrationMessage.shouldBe(Condition.visible.because("После успешной регистрации должно отобразиться уведомление об успешной регистрации"), Duration.ofSeconds(5));
        signInBtn.shouldBe(Condition.visible.because("После успешной регистрации должна отобразиться кнопка Sign In"), Duration.ofSeconds(5));
        signInBtn.click();
        return new LoginPage();
    }

    public void assertUsernameTaken() {
        usernameExistsMessage.shouldBe(Condition.visible.because("Если username занят, должно отобразиться уведомление об этом"), Duration.ofSeconds(5));
    }

}
