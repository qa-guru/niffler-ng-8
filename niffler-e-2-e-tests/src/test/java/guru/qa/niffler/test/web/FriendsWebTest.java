package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;
import static guru.qa.niffler.page.AllPeoplePage.Status;

@WebTest
public class FriendsWebTest {
    private static final Config CFG = Config.getInstance();
    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .assertFriendExist(user.friend());
    }
    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .assertEmptyUser();
    }
    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .assertFriendRequestExist(user.income());
    }
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .clickAllPeopleTab()
                .assertUserStatus(user.outcome(), Status.REQUEST_SEND);
    }
}
