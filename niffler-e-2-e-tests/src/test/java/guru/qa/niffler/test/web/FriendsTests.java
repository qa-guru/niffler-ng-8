package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.UserType;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.jupiter.extensions.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotations.UserType.Type.*;

@ExtendWith(BrowserExtension.class)
public class FriendsTests {
    private static final Config CFG = Config.getInstance();

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendShouldBePresentFriendsTable(@UserType(type = WITH_FRIEND) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.password())
            .iconSubmit()
            .friendsSubmit()
            .shouldFriendVisible(user.friend())
            .shouldUnfriendButtonVisible(user.friend());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendsTableShouldBeEmptyForNewUser(@UserType(type = EMPTY) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.password())
            .iconSubmit()
            .friendsSubmit()
            .shouldFriendsNotVisible();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void incomeInvitationBePresentInFriendsTable(@UserType(type = WITH_INCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.password())
            .iconSubmit()
            .friendsSubmit()
            .allPeopleTabSubmit()
            .shouldInviteVisible(user.income());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void outcomeInvitationBePresentInFriendsTable(@UserType(type = WITH_OUTCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.password())
            .iconSubmit()
            .friendsSubmit()
            .shouldAcceptButtonVisibleAndEnabled(user.outcome())
            .shouldDeclineButtonVisibleAndEnabled(user.outcome());
    }
}
