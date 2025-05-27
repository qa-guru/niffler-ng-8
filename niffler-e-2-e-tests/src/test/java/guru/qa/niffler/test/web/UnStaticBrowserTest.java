package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Browsers;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.NonStaticWebTest;
import guru.qa.niffler.jupiter.extension.BrowserProvider;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.Browser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

@NonStaticWebTest
public class UnStaticBrowserTest {

    private static final Config CFG = Config.getInstance();
    @ParameterizedTest
    @EnumSource(Browser.class)
    @User
    void testInBrowser(@ConvertWith(BrowserProvider.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .doLogin(user)
                .getHeader()
                .toFriendsPage()
                .assertEmptyUser();
    }

    @User(incomeInvitations = 1)
    @Browsers({Browser.CHROME, Browser.CHROME})
    @Test
    void invitationTest(SelenideDriver[] drivers, UserJson user) {
        SelenideDriver userDriver = drivers[0];
        SelenideDriver friendDriver = drivers[1];
        userDriver.open(CFG.frontUrl());
        new LoginPage(userDriver)
                .doLogin(user)
                .getHeader()
                .toFriendsPage()
                .assertFriendRequestExist(user);
        friendDriver.open(CFG.frontUrl());
        new LoginPage(friendDriver)
                .doLogin(user.testData().incomeInvitations().getFirst())
                .getHeader()
                .toFriendsPage()
                .clickAllPeopleTab()
                .assertUserStatus(user.username(), AllPeoplePage.Status.REQUEST_SEND);
    }
}
