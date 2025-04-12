package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.Test;

public class RegistrationTest {
  private static final Faker faker = new Faker();

  @Test
  void shouldRegisterNewUser() {
    final String username = faker.name().username();
    final String password = faker.internet().password(3, 12);

    Selenide.open(RegistrationPage.URL, RegistrationPage.class)
      .doRegister(username, password, password)
      .doLogin(username, password)
      .verifyMainComponentsVisible();
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    final String username = "NiceGuy";
    final String password = faker.internet().password(3, 12);

    Selenide.open(RegistrationPage.URL, RegistrationPage.class)
      .setUsername(username)
      .setPassword(password)
      .setPasswordSubmit(password)
      .clickSignUp()
      .verifyErrorDisplayed("Username `" + username + "` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    Selenide.open(RegistrationPage.URL, RegistrationPage.class)
      .setUsername(faker.name().username())
      .setPassword(faker.internet().password(3, 12))
      .setPasswordSubmit(faker.internet().password(3, 12))
      .clickSignUp()
      .verifyErrorDisplayed("Passwords should be equal");
  }
}
