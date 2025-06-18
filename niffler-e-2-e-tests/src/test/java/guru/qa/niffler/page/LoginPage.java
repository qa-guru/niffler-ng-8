package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitBtn;
    private final SelenideElement createNewAccountBtn;
    private final SelenideElement badCredentialsValidationError;

    public LoginPage(SelenideDriver driver) {
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitBtn = driver.$("button[type='submit']");
        this.createNewAccountBtn = driver.$(".form__register");
        this.badCredentialsValidationError = driver.$(".form__error");
        WebDriverRunner.setWebDriver(driver.getWebDriver());
    }

    public LoginPage() {
        this.usernameInput = $("input[name='username']");
        this.passwordInput = $("input[name='password']");
        this.submitBtn = $("button[type='submit']");
        this.createNewAccountBtn = $(".form__register");
        this.badCredentialsValidationError = $(".form__error");
    }

    @Nonnull
    public MainPage doLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitBtn
                .click();
        return new MainPage();
    }

    public RegisterPage clickCreateNewAccount() {
        createNewAccountBtn
                .click();
        return new RegisterPage();
    }

    public void checkLoginPageShouldBeLoaded() {
        $("h1").shouldHave(text("Log in"));
    }

    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public LoginPage clickSubmitBtn() {
        submitBtn
                .click();
        return this;
    }

    @Nonnull
    public void checkBadCredentialsValidationErrorIsDisplayed(String errorMessage) {
        badCredentialsValidationError.shouldHave(text(errorMessage));
    }
}
