package guru.qa.niffler.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement logInBtn = $("button[type='submit']");
  private final SelenideElement createNewUserBtn = $("a.form__register");
  private final SelenideElement badCredentialsError = $(".form__error-container .form__error");

  public MainPage doLoginSuccess(String username, String password) {
    doLogin(username, password);
    return new MainPage();
  }

  public LoginPage doLoginError(String username, String password) {
    doLogin(username, password);
    return this;
  }

  private LoginPage doLogin(String username, String password) {
    setUsername(username);
    setPassword(password);
    clickLogInBtn();
    return this;
  }

  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public LoginPage clickLogInBtn() {
    logInBtn.click();
    return this;
  }

  public RegisterPage clickCreateNewUserBtn() {
    createNewUserBtn.click();
    return new RegisterPage();
  }

  public LoginPage checkBadCredentialsError() {
    badCredentialsError.shouldBe(visible);
    badCredentialsError.shouldHave(text("Неверные учетные данные пользователя"));
    return this;
  }

}
