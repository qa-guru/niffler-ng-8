package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.web.page.*;

@WebTest
public class BaseWebTest {

    protected static final Config CFG = Config.getInstance();

    protected LoginPage openLoginPage() {
        return Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    protected ProfilePage openProfilePage() {
        return openMainPage()
            .getHeader().goToProfilePage();
    }

    protected static MainPage openMainPage() {
        return Selenide.open(CFG.frontUrl(), MainPage.class);
    }

    protected LoginPage openLoginPage(SelenideDriver driver) {
        driver.open(CFG.frontUrl());
        return new LoginPage(driver);
    }

    protected RegisterPage openRegisterPage() {
        return openLoginPage()
            .clickCreateNewUserBtn();
    }

    protected FriendsPage openFriendsPage() {
        return openMainPage()
            .getHeader().goToFriendsPage();
    }
}