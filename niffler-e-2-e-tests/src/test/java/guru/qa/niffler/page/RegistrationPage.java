package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
  private final SelenideElement usernameInput = $("input[id='username']");
  private final SelenideElement passwordInput = $("input[id='password']");
  private final SelenideElement passwordSubmitInput = $("button[id='passwordSubmit']");
  private final SelenideElement signUpButton = $("button[type='submit']");
  private final SelenideElement signInButton = $("a.form_sign-in");

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

  public LoginPage submitRegistration(){
    signUpButton.click();
    signInButton.shouldBe(visible);
    signInButton.click();
    return new LoginPage();
  }
}