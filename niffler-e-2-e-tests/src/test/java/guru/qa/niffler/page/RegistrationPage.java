package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegistrationPage {

  public static final String URL = Config.getInstance().authUrl() + "register";

  private final SelenideElement usernameInput = $("input[id='username']");
  private final SelenideElement passwordInput = $("input[id='password']");
  private final SelenideElement passwordSubmitInput = $("input[id='passwordSubmit']");
  private final SelenideElement signUpButton = $("button[type='submit']");
  private final SelenideElement signInButton = $("a.form_sign-in");
  private final SelenideElement errorMessage = $("span.form__error");

  public RegistrationPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public RegistrationPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public RegistrationPage setPasswordSubmit(String password) {
    passwordSubmitInput.setValue(password);
    return this;
  }

  public RegistrationPage clickSignUp() {
    signUpButton.click();
    return this;
  }

  public LoginPage doRegister(String username, String password, String passwordSubmit) {
    setUsername(username);
    setPassword(password);
    setPasswordSubmit(passwordSubmit);
    clickSignUp();
    signInButton.shouldBe(visible);
    signInButton.click();
    return new LoginPage();
  }

  public RegistrationPage verifyErrorDisplayed(String expectedError) {
    errorMessage.shouldBe(visible).shouldHave(exactText(expectedError));
    return this;
  }
}