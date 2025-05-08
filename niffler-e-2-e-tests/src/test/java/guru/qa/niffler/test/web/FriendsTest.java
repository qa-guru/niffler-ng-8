package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @User(friends = 1)
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .friendsPage()
                .checkExistingFriends(String.valueOf(user.testData().friends()));
    }

    @Test
    @User
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .friendsPage()
                .checkNoExistingFriends();
    }

    @Test
    @User(incomeInvitations = 1)
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .friendsPage()
                .checkExistingInvitations(String.valueOf(user.testData().incomeInvitations()));
    }

    @Test
    @User(outcomeInvitations = 1)
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .allPeoplesPage()
                .checkInvitationSentToUser(String.valueOf(user.testData().outcomeInvitations()));
    }

    @Test
    @User
    void shouldBeAbleToSendFriendRequest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .allPeoplesPage()
                .sendFriendRequestTo("dima")
                .checkInvitationSentToUser("dima");
    }
}
