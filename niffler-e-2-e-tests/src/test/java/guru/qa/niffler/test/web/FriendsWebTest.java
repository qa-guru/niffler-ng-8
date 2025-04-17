package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.EMPTY;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_FRIEND;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_INCOME_REQUEST;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_OUTCOME_REQUEST;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;

@ExtendWith(BrowserExtension.class)
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendsShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .loadingFriendsPage()
                .checkFriendWithName(user.friend());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .loadingFriendsPage()
                .checkEmptyFriendsList();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .loadingFriendsPage()
                .checkIncomeInvitation(user.income());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .loadingFriendsPage()
                .checkOutcomeInvitation(user.outcome());
    }

}
