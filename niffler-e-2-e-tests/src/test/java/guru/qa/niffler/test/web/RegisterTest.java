package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;

import static utils.FakerGenUtil.*;


public class RegisterTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        String newUser = genRandomName();
        String password = genRandomPassword();

        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .doRegister(newUser, password)
                .checkMessageOfSuccessfulRegistration("Congratulations! You've registered!");
    }

}
