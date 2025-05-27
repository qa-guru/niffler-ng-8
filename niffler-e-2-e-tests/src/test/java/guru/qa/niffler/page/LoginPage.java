package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.ElementType;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byText;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage {

  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement submitBtn;
  private final SelenideElement registerButton;
  private final SelenideElement showPasswordIcon;
  private final SelenideElement errorComponent;

  public LoginPage(@Nullable SelenideDriver driver) {
    super(driver);
    this.usernameInput = $("input[name='username']");
    this.passwordInput = $("input[name='password']");
    this.submitBtn = $("button[type='submit']");
    this.registerButton = $("a[class='form__register']");
    this.showPasswordIcon = $("button[class=form__password-button]");
    this.errorComponent = $(byText("Неверные учетные данные пользователя"));
  }

  public LoginPage() {
    this(null);
  }

  @Step("Log in with user {username}")
  public MainPage doLogin(String username, String password) {
    return setUserName(username)
            .setPassword(password)
            .clickLogin();
  }

  @Step("Log in with user {user}")
  public MainPage doLogin(UserJson user) {
    return setUserName(user.username())
            .setPassword(user.testData().password())
            .clickLogin();
  }

  @Step("Set username {username} into input")
  public LoginPage setUserName(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password {password} into input")
  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Click log in button")
  public MainPage clickLogin(){
    submitBtn.click();
    return new MainPage(driver);
  }

  @Step("Click register button")
  public RegistrationPage clickRegister() {
    registerButton.click();
    return new RegistrationPage(driver);
  }

  @Step("assert password type is {type}")
  public LoginPage assertPasswordType(ElementType type){
    passwordInput.shouldHave(type.assertType());
    return this;
  }

  @Step("Click show password icon")
  public LoginPage showPassword() {
    showPasswordIcon.click();
    return this;
  }

  @Step("Assert password")
  public LoginPage assertPassword(String password) {
    passwordInput.shouldHave(value(password));
    return this;
  }

  @Step("assert no redirect to main page")
  public LoginPage assertNoRedirectToMainPage(){
    submitBtn.shouldBe(Condition.visible, Duration.ofSeconds(5));
    return this;
  }

  @Step("Assert error is visible")
  public LoginPage assertError(){
    errorComponent.shouldBe(Condition.visible,Duration.ofSeconds(5));
    return this;
  }
}
