package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.test.web.BaseTest.CFG;

@WebTest
public class FriendsTest {

    @Test
    @User(friends = 1)
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .goToFriendsTab()
                .verifyUserVisibleInFriends(String.valueOf(user.testData().friends().getFirst().username()));
    }

    @Test
    @User
    void friendTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .goToFriendsTab()
                .verifyFriendsListIsEmpty();
    }

    @Test
    @User(incomeInvitations = 1)
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .goToFriendsTab()
                .verifyUserVisibleInRequests(String.valueOf(user.testData().incomeInvitations().getFirst().username()));
    }

    @Test
    @User(outcomeInvitations = 1)
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .goToAllPeopleTab()
                .verifyUserOutcomeVisibleInList(String.valueOf(user.testData().outcomeInvitations().getFirst().username()));
    }
}
