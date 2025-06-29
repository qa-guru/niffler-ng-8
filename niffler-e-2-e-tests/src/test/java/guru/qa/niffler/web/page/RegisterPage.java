package guru.qa.niffler.web.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement singUpBtn = $("button[type='submit']");
    private final SelenideElement usernameErrorText = $("input[name='username'] ~ .form__error");
    private final SelenideElement passwordErrorText = $("input[name='password'] ~ .form__error");

    public SuccessfulRegistrationPage registerUserSuccess(String username, String password, String passwordSubmit) {
        registerUser(username, password, passwordSubmit);
        return new SuccessfulRegistrationPage();
    }

    public RegisterPage registerUserError(String username, String password, String passwordSubmit) {
        registerUser(username, password, passwordSubmit);
        return this;
    }

    @Step("Регистрируем пользователя")
    public RegisterPage registerUser(String username, String password, String passwordSubmit) {
        setUsername(username);
        setPassword(password);
        setPasswordSubmit(passwordSubmit);
        clickSignUpBtn();
        return this;
    }

    @Step("Вводим имя")
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Вводим пароль")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Подтверждаем пароль")
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    @Step("Кликаем кнопку авторизоваться")
    public RegisterPage clickSignUpBtn() {
        singUpBtn.click();
        return this;
    }

    @Step("Проверяем ошибку в поле имени")
    public RegisterPage checkUsernameError(String expErrorText) {
        usernameErrorText.shouldBe(visible);
        usernameErrorText.shouldHave(text(expErrorText));
        return this;
    }

    @Step("Проверяем ошибку в поле пароля")
    public RegisterPage checkPasswordError(String expErrorText) {
        passwordErrorText.shouldBe(visible);
        passwordErrorText.shouldHave(text(expErrorText));
        return this;
    }
}
