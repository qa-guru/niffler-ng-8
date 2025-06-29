package guru.qa.niffler.web.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.api.model.UserParts;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class LoginPage extends BasePage<LoginPage> {

  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement logInBtn;
  private final SelenideElement createNewUserBtn;
  private final SelenideElement badCredentialsError;

  public LoginPage(SelenideDriver driver) {
    usernameInput = driver.$("input[name='username']");
    passwordInput = driver.$("input[name='password']");
    logInBtn = driver.$("button[type='submit']");
    createNewUserBtn = driver.$("a.form__register");
    badCredentialsError = driver.$(".form__error-container .form__error");
  }

  public LoginPage() {
    usernameInput = Selenide.$("input[name='username']");
    passwordInput = Selenide.$("input[name='password']");
    logInBtn = Selenide.$("button[type='submit']");
    createNewUserBtn = Selenide.$("a.form__register");
    badCredentialsError = Selenide.$(".form__error-container .form__error");
  }


  public MainPage doLoginSuccess(String username, String password) {
    doLogin(username, password);
    return new MainPage();
  }

  public MainPage doLoginSuccess(UserParts user) {
    doLogin(user.getUsername(), user.getPassword());
    return new MainPage();
  }

  public LoginPage doLoginError(String username, String password) {
    doLogin(username, password);
    return this;
  }

  @Step("Авторизуемся")
  private LoginPage doLogin(String username, String password) {
    setUsername(username);
    setPassword(password);
    clickLogInBtn();
    return this;
  }

  @Step("Вписываем имея пользователя")
  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Вписываем пароль")
  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Кликаем на кнопку 'LogIn'")
  public LoginPage clickLogInBtn() {
    logInBtn.click();
    return this;
  }

  @Step("Кликаем на кнопку 'Create new user'")
  public RegisterPage clickCreateNewUserBtn() {
    createNewUserBtn.click();
    return new RegisterPage();
  }

  @Step("Проверяем ошибку авторизации")
  public LoginPage checkBadCredentialsError() {
    badCredentialsError.shouldBe(visible);
    badCredentialsError.shouldHave(
        text("Неверные учетные данные пользователя")
            .or(text("Bad credentials"))
    );
    return this;
  }
}
