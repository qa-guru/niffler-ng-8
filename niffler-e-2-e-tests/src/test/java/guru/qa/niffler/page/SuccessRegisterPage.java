package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class SuccessRegisterPage {
    private final SelenideElement successfulRegistrationText = $("[class*='paragraph_success']");
    private final SelenideElement loginSubmit = $("[class='form_sign-in']");

    public SuccessRegisterPage shouldRegistrationText() {
        successfulRegistrationText.shouldHave(Condition.text("Congratulations! You've registered!"));

        return new SuccessRegisterPage();
    }
}
