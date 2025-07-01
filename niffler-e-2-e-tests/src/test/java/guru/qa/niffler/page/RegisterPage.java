package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage extends BasePage{

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement signUpBtn = $x("//button[contains(text(),'Sign Up')]");
    private final SelenideElement signInBtn = $x("//a[contains(text(),'Sign in')]");
    private final SelenideElement error = $x("//span[contains(@class,'form__error')]");

    public RegisterPage setUsername(String username) {
        usernameInput.sendKeys(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.sendKeys(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.sendKeys(password);
        return this;
    }

    public RegisterPage signUp() {
        signUpBtn.click();
        return this;
    }

    public RegisterPage signIn() {
        signInBtn.click();
        return this;
    }

    public RegisterPage checkError(String errorText) {
        error.shouldHave(Condition.text(errorText));
        return this;
    }
}
