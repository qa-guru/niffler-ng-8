package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.constants.ErrorMessages.USERNAME_INCORRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginPage {

  public static final String URL = Config.getInstance().frontUrl() + "login";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement registerButton = $x("//a[@href='/register']");
  private final SelenideElement userIncorrectText = $x("//p[@class='form__error' and contains (text(),'Неверные ')]");

  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage();
  }

  public RegisterPage doRegister() {
    registerButton.click();
    return new RegisterPage();
  }

  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public LoginPage submitLogin() {
    submitBtn.click();
    return this;
  }

  public void checkThatUserIsNotCorrect() {
    userIncorrectText.shouldHave(text("Неверные учетные данные пользователя"));
  }

  public void checkThatUsernameIsNotFilled() {
    String validationMessage = usernameInput.getAttribute("validationMessage");
    assertEquals(USERNAME_INCORRECT, validationMessage);
  }

}
