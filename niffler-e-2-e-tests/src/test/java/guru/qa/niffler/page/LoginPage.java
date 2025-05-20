package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.ElementType;
import guru.qa.niffler.model.UserJson;

import java.time.Duration;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byText;

public class LoginPage extends BasePage {

  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement submitBtn;
  private final SelenideElement registerButton;
  private final SelenideElement showPasswordIcon;
  private final SelenideElement errorComponent;

  public LoginPage(SelenideDriver driver) {
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

  public MainPage doLogin(String username, String password) {
    return setUserName(username)
            .setPassword(password)
            .clickLogin();
  }

  public MainPage doLogin(UserJson user) {
    return setUserName(user.username())
            .setPassword(user.testData().password())
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
    return new MainPage(driver);
  }

  public RegistrationPage clickRegister() {
    registerButton.click();
    return new RegistrationPage(driver);
  }

  public LoginPage assertPasswordType(ElementType type){
    passwordInput.shouldHave(type.assertType());
    return this;
  }
  public LoginPage showPassword() {
    showPasswordIcon.click();
    return this;
  }
  public LoginPage assertPassword(String password) {
    passwordInput.shouldHave(value(password));
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
