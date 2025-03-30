package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends BasePage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement createNewAccountBtn = $x("//a[contains(text(), 'Create new account')]");
    private final SelenideElement errorBaseLocator = $x("//span[contains(text(), '%s')]");

    public MainPage doLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitBtn.click();
        return new MainPage();
    }

    public RegisterPage goToRegistration() {
        createNewAccountBtn.click();
        return new RegisterPage();
    }



}
