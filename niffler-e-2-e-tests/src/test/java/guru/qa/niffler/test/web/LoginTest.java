package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

public class LoginTest {
  private static final String username = "NiceGuy";
  private static final String password = "qwer";

  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(username, password)
      .verifyMainComponentsVisible();
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
