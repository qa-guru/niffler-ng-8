package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.constants.ErrorMessages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.constants.ErrorMessages.PASSWORD_LENGTH_INCORRECT;
import static guru.qa.niffler.constants.ErrorMessages.USERNAME_LENGTH_INCORRECT;

public class RegisterPage {

    private final SelenideElement usernameInput = $x("//input[@id='username']");
    private final SelenideElement passwordInput = $x("//input[@id='password']");
    private final SelenideElement passwordSubmitInput = $x("//input[@id='passwordSubmit']");
    private final SelenideElement signUpButton = $x("//button[@type='submit']");
    private final SelenideElement usernameAlreadyExistsText = $x("//span[@class='form__error' and contains(text(), 'already')]");
    private final SelenideElement passwordShouldBeEqualText = $x("//span[@class='form__error' and contains(text(), 'Passwords should be equal')]");
    private final SelenideElement userNameLengthIncorrectText = $x("//span[@class='form__error' and contains(text(), 'Allowed username length should be from 3 to 50 characters')]");
    private final SelenideElement passwordLengthIncorrectText = $x("//span[@class='form__error' and contains(text(), 'Allowed password length should be from 3 to 12 characters')]");


    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    public RegisterPage submitRegistration() {
        signUpButton.click();
        return this;
    }

    public SuccessfulRegistrationPage doRegister(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(password);
        signUpButton.click();
        return new SuccessfulRegistrationPage();
    }

    public void checkUserNameIsAlreadyExist() {
        usernameAlreadyExistsText.shouldHave(text("already exists"));
    }

    public void checkPasswordsAreNotEquals() {
        passwordShouldBeEqualText.shouldHave(text("Passwords should be equal"));
    }

    public void checkUserNameLengthIncorrect() {
        userNameLengthIncorrectText.shouldHave(text(USERNAME_LENGTH_INCORRECT));
    }

    public void checkPasswordLengthIncorrect() {
        passwordLengthIncorrectText.shouldHave(text(PASSWORD_LENGTH_INCORRECT));
    }

}
