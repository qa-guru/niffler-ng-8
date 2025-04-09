package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.users.UsersQueueExtension;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement createNewAccount = $x("//*[contains(text(),'Create new account')]");
  private final SelenideElement error = $x("//p[contains(@class,'form__error')]");

  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage();
  }

  public MainPage doLogin(UsersQueueExtension.StaticUser user) {
   doLogin(user.userName(), user.pass());
    return new MainPage();
  }

  public LoginPage clickCreateNewAccount() {
    createNewAccount.click();
    return new LoginPage();
  }

  public LoginPage checkError(String errorText) {
    error.shouldHave(Condition.text(errorText));
    return this;
  }
}
