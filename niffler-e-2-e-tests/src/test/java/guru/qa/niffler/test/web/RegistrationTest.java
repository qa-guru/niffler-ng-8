package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.DoRegister;
import guru.qa.niffler.model.ElementType;
import guru.qa.niffler.model.User;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegistrationPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@WebTest
class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private static final int minValidPassword=3;
    private static final int maxValidPassword=12;
    private static final int minValidUsername=3;
    private static final int maxValidUsername=50;
    private String username;
    private String password;

    @BeforeEach
    void setUp() {
        username = RandomDataUtils.randomEmail();
        password = RandomDataUtils.randomPassword(minValidPassword,maxValidPassword);
    }

    @Test
    @DisplayName("Проверить успешную регистрацию")
    void testSuccessRegistration() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .clickSignUp()
                .clickSignIn()
                .assertRedirectToPage(LoginPage.class)
                .doLogin(username,password);
    }

    @Test
    @DisplayName("Проверить корректный редирект при активации гиперссылки логин")
    void testSuccessLoginRedirect() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .redirectToLogin()
                .assertRedirectToPage(LoginPage.class);
    }

    @Test
    @DisplayName("Проверить корректное отображение и ввод пароля")
    void testCorrectPasswordDisplay() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .assertPassword(password)
                .assertPasswordType(ElementType.PASSWORD)
                .showPassword()
                .assertPasswordType(ElementType.TEXT)
                .assertPasswordSubmit(password)
                .assertPasswordSubmitType(ElementType.PASSWORD)
                .showPasswordSubmit()
                .assertPasswordSubmitType(ElementType.TEXT);
    }

    @Test
    @DisplayName("Проверить отображение ошибки при попытке создать пользователя с занятым логином")
    @DoRegister
    void testUserAlreadyExist(User user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(user.username())
                .setPassword(user.password())
                .setPasswordSubmit(user.password())
                .clickSignUp()
                .assertUserExistError(user.username());
    }

    static Stream<Arguments> validationTest() {
        return Stream.of(
                Arguments.of(
                        "Проверить, что имя пользователя не может состоять из пробелов",
                        "     ",
                        RandomDataUtils.randomPassword(minValidPassword,maxValidPassword),
                        (Runnable) () ->  new RegistrationPage().assertUsernameCannotBlank()),

                Arguments.of(
                        "Проверить, что имя пользователя не может включать из пробелы",
                        " 12  34  ",
                        RandomDataUtils.randomPassword(minValidPassword,maxValidPassword),
                        (Runnable) () ->  new RegistrationPage().assertNoRedirectToSignInPage()),

                Arguments.of(
                        "Проверить, что пароль пользователя не может состоять из пробелов",
                        RandomDataUtils.randomEmail(),
                        "     ",
                        (Runnable) () ->  new RegistrationPage().assertPasswordCannotBlank()),

                Arguments.of(
                        "Проверить, что пароль не может включать пробелы",
                        RandomDataUtils.randomEmail(),
                        " 12  34  ",
                        (Runnable) () ->  new RegistrationPage().assertNoRedirectToSignInPage()),

                Arguments.of(
                        "Проверить, что имя пользователя не может быть длиной менее 3 символов",
                        RandomDataUtils.randomString(minValidUsername-1),
                        RandomDataUtils.randomPassword(minValidPassword,maxValidPassword),
                        (Runnable) () ->  new RegistrationPage().assertUsernameLength()),

                Arguments.of(
                        "Проверить, что имя пользователя не может быть длиной более 50 символов",
                        RandomDataUtils.randomString(maxValidUsername+1),
                        RandomDataUtils.randomPassword(minValidPassword,maxValidPassword),
                        (Runnable) () ->  new RegistrationPage().assertUsernameLength()),

                Arguments.of(
                        "Проверить, что пароль пользователя не может быть длиной менее 3 символов",
                        RandomDataUtils.randomEmail(),
                        RandomDataUtils.randomString(minValidPassword-1),
                        (Runnable) () ->  new RegistrationPage().assertPasswordLength()),

                Arguments.of(
                        "Проверить, что пароль пользователя не может быть длиной более 12 символов",
                        RandomDataUtils.randomEmail(),
                        RandomDataUtils.randomString(maxValidPassword+1),
                        (Runnable) () ->  new RegistrationPage().assertPasswordLength()),
                Arguments.of(
                        "Проверить, что имя пользователя - обязательное поле",
                        "",
                        RandomDataUtils.randomString(maxValidPassword),
                        (Runnable) () ->  new RegistrationPage().assertNoRedirectToSignInPage())
                );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validationTest")
    void testRegistrationValidation(String name,String username, String password, Runnable assertion) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .clickSignUp();
                assertion.run();
    }


    @Test
    @DisplayName("Проверить, что пароли в обоих полях должны совпадать")
    void testPasswordShouldBeEqual() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(username)
                .setPassword(password)
                .setPasswordSubmit(password+"X")
                .clickSignUp()
                .assertPasswordShouldBeEqual();
    }

    @Test
    @DisplayName("Проверить, что пароль пользователя - обязательное поле")
    void testPasswordRequired() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(username)
                .setPasswordSubmit(password)
                .clickSignUp()
                .assertNoRedirectToSignInPage();
    }

    @Test
    @DisplayName("Проверить, что подтверждение пользователя  - обязательное поле")
    void testPasswordSubmitRequired() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegister()
                .assertRedirectToPage(RegistrationPage.class)
                .setUserName(username)
                .setPassword(password)
                .clickSignUp()
                .assertNoRedirectToSignInPage();
    }
}