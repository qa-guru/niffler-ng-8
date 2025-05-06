package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {
    private static final Config CFG = Config.getInstance();
    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .openFriendsPage()
                .assertFriendExist(user);
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .openFriendsPage()
                .assertEmptyUser();
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .openFriendsPage()
                .assertFriendRequestExist(user);
    }
    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .openFriendsPage()
                .clickAllPeopleTab()
                .assertOutcomeInvitations(user);
    }
}
