package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.PasswordType;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginPage extends BasePage{

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement registerButton = $("a[class='form__register']");
  private static SelenideElement showPasswordIcon = $("button[class=form__password-button]");
  private static SelenideElement errorComponent = $(byText("Неверные учетные данные пользователя"));

  public MainPage doLogin(String username, String password) {
    return this.setUserName(username)
            .setPassword(password)
            .clickLogin();
  }

  public LoginPage setUserName(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public MainPage clickLogin(){
    submitBtn.click();
    return new MainPage();
  }

  public RegistrationPage clickRegister() {
    registerButton.click();
    return new RegistrationPage();
  }

  public LoginPage assertPasswordType(PasswordType type){
    assertEquals(type.toString(),passwordInput.getAttribute("type"));
    return this;
  }
  public LoginPage showPassword() {
    showPasswordIcon.click();
    return this;
  }
  public LoginPage assertPassword(String password) {
    Assertions.assertEquals(passwordInput.val(),password);
    return this;
  }

  public LoginPage assertNoRedirectToMainPage(){
    submitBtn.shouldBe(Condition.visible, Duration.ofSeconds(5));
    return this;
  }

  public LoginPage assertError(){
    errorComponent.shouldBe(Condition.visible,Duration.ofSeconds(5));
    return this;
  }
}
