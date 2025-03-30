package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin("Ilya", "12345")
        .checkThatMainPageIsLoaded();
  }

  @Test
  void shouldShowMessageIfUsernameIsNotFilled() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .setPassword("a")
        .submitLogin()
        .checkThatUsernameIsFilled();
  }

  @Test
  void shouldShowMessageIfPasswordIsNotFilled() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .setUsername("a")
        .submitLogin()
        .checkThatPasswordIsFilled();
  }

  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .setUsername("a")
        .setPassword("b")
        .submitLogin()
        .checkThatUserIsNotCorrect();
  }

}
