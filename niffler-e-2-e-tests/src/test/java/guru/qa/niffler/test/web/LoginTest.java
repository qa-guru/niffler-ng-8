package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {
  private static final String username = "NiceGuy";
  private static final String password = "qwer";

  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(username, password);
  }

  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .setUsername(username)
      .setPassword("wrongPassword")
      .clickSubmit()
      .verifyErrorDisplayed("Неверные учетные данные пользователя");
  }
}
