package guru.qa.niffler.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class SuccessfulRegistrationPage extends BasePage {

    private final SelenideElement successText = $(".form__paragraph_success");
    private final SelenideElement singInBtn = $("a.form_sign-in");

    public LoginPage clickSingInBtn() {
        singInBtn.click();
        return new LoginPage();
    }

    public LoginPage checkSuccessfulRegistrationPage() {
        successText.shouldBe(visible);
        successText.shouldHave(text("Congratulations! You've registered!"));
        singInBtn.shouldBe(visible);
        singInBtn.shouldBe(clickable);
        singInBtn.shouldHave(text("Sign in"));
        return new LoginPage();
    }

}
