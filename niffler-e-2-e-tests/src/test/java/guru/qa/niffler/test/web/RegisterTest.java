package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;


public class RegisterTest {

    private static final Config CFG = Config.getInstance();
    public Faker faker = new Faker();
    @Test
    void shouldRegisterNewUser() {
        String newUser = faker.name().fullName();
        String password = faker.internet().password(3,10);

        RegisterPage registerPage =
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .doRegister();

        registerPage
                .doRegister(newUser, password)
                .checkMessageOfSuccessfulRegistration("Congratulations! You've registered!");
    }

}
