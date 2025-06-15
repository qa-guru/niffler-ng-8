package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.web.page.FriendsPage;
import guru.qa.niffler.web.page.LoginPage;
import guru.qa.niffler.web.page.RegisterPage;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class BaseWebTest {

    protected static final Config CFG = Config.getInstance();

    protected LoginPage openLoginPage() {
        return Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    protected LoginPage openLoginPage(SelenideDriver driver) {
        driver.open(CFG.frontUrl());
        return new LoginPage(driver);
    }

    protected RegisterPage openRegisterPage() {
        return openLoginPage()
            .clickCreateNewUserBtn();
    }

    protected FriendsPage openFriendsPage(StaticUser user) {
        return openLoginPage()
            .doLoginSuccess(user.username(), user.password())
            .getHeader().goToFriendsPage();
    }

    protected FriendsPage openFriendsPage(UserParts user) {
        return openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
            .getHeader().goToFriendsPage();
    }

}