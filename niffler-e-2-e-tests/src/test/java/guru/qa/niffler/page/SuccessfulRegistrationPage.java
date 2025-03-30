package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class SuccessfulRegistrationPage {
  private final SelenideElement congratsField = $("p.form__paragraph_success");
  private final SelenideElement signInBtn = $(".form_sign-in");

  public SuccessfulRegistrationPage checkMessageOfSuccessfulRegistration(String message) {
    congratsField.shouldHave(text(message));
    return this;
  }

  public LoginPage signIn() {
    signInBtn.click();
    return new LoginPage();
  }
}
