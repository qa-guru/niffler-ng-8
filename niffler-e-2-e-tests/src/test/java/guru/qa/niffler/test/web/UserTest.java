package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.jupiter.extensions.UsersQueueExtension;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.SidebarPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extensions.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extensions.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extensions.UsersQueueExtension.UserType.Type.*;
import static guru.qa.niffler.page.FriendsPage.FriendType.*;

@ExtendWith({BrowserExtension.class})
public class UserTest {
    private static final Config CFG = Config.getInstance();

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class).doLogin(user);
        new SidebarPage().clickMenu().clickFriends();
        new FriendsPage().checkTableContainsFriendWithStatus(user.userName(), FRIEND);
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class).doLogin(user);
        new SidebarPage().clickMenu().clickFriends();
        new FriendsPage().checkTableEmpty();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class).doLogin(user);
        new SidebarPage().clickMenu().clickFriends();
        new FriendsPage().checkTableContainsFriendWithStatus(user.userName(), INCOME);
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class).doLogin(user);
        new SidebarPage().clickMenu().clickFriends();
        new FriendsPage().checkTableContainsFriendWithStatus(user.userName(), OUTCOME);
    }
}
