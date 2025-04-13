package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  public static final String URL = Config.getInstance().authUrl() + "login";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement createNewAccountButton = $("a.form__register");
  private final SelenideElement errorMessage = $("p.form__error");

  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage().verifyMainComponentsVisible();
  }

  public RegistrationPage clickCreateNewAccount() {
    createNewAccountButton.click();
    return new RegistrationPage();
  }

  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public LoginPage clickSubmit() {
    submitBtn.click();
    return this;
  }

  public LoginPage verifyErrorDisplayed(String expectedError) {
    errorMessage.shouldBe(visible).shouldHave(exactText(expectedError));
    return this;
  }
}
