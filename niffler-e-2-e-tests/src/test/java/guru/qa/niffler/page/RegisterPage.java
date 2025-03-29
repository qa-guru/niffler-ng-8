package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitPasswordInput = $("#passwordSubmit");
    private final SelenideElement signUpBtn = $("button[class='form__submit']");
    private final SelenideElement signInBtn = $("button[class='form_sign-in']");


    public RegisterPage submitRegistration(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitPasswordInput.setValue(password);
        signUpBtn.click();
        return this;
        // return new MainPage();
    }

    public LoginPage signInAfterRegistration() {
        signInBtn.shouldBe(Condition.visible.because("После нажатия кнопки Sign Up должна отобразиться кнопка Sign In"));
        signInBtn.click();
        return new LoginPage();
    }
}
