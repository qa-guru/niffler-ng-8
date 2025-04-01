package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitButton = $("[class='form__submit']");
    private final ElementsCollection errorsText = $$("[class='form__error']");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);

        return new RegisterPage();
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);

        return new RegisterPage();
    }

    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);

        return new RegisterPage();
    }

    public SuccessRegisterPage submitRegistration() {
        submitButton.click();

        return new SuccessRegisterPage();
    }

    public RegisterPage shouldErrorsText() {
        errorsText.findBy(text("Passwords should be equal Allowed password length should be from 3 to 12 characters")).shouldBe(visible);

        return new RegisterPage();
    }
}
