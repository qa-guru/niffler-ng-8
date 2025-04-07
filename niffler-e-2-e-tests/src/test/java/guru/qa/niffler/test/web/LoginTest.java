package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.DoRegister;
import guru.qa.niffler.model.ElementType;
import guru.qa.niffler.model.User;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {
    private static final Config CFG = Config.getInstance();
    private static final String ERROR_PATH = "?error";


    @Test
    @DisplayName("Проверить успешную авторизацию")
    @DoRegister
    void testSuccessLogin(User user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .assertRedirectToPage(MainPage.class)
                .assertMainComponents();
    }

    @Test
    @DisplayName("Проверить корректное отображение и ввод пароля")
    void testCorrectPasswordDisplay() {
        String password = "! 123avc$%SD";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setPassword(password)
                .assertPassword(password)
                .assertPasswordType(ElementType.PASSWORD)
                .showPassword()
                .assertPasswordType(ElementType.TEXT);
    }

    @Test
    @DisplayName("Проверить, что при вводе пробелов в логин, они не учитываются")
    @DoRegister
    void testSuccessLoginWithSpaces(User user) {
        String username = "   "+user.username()+"   ";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(username, user.password())
                .assertRedirectToPage(MainPage.class);
    }

    @Test
    @DisplayName("Проверить, что поле Username - обязательное")
    void testUsernameRequired() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setPassword("password")
                .clickLogin()
                .assertRedirectToPage(LoginPage.class)
                .assertNoRedirectToMainPage();
    }

    @Test
    @DisplayName("Проверить, что поле Password - обязательное")
    void testPasswordRequired() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUserName("username")
                .clickLogin()
                .assertRedirectToPage(LoginPage.class)
                .assertNoRedirectToMainPage();
    }

    @Test
    @DisplayName("Проверить авторизацию с не существующим логином")
    @DoRegister
    void testIncorrectLogin(User user) {
        String userName = user.username()+"123";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(userName, user.password())
                .assertRedirectToPage(LoginPage.class,ERROR_PATH)
                .assertError();
    }

    @Test
    @DisplayName("Проверить авторизацию с не верным паролем")
    @DoRegister
    void testIncorrectPassword(User user) {
        String password = user.password()+"123";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), password)
                .assertRedirectToPage(LoginPage.class,ERROR_PATH)
                .assertError();
    }
}
