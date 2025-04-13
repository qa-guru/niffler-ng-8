package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;
import static guru.qa.niffler.jupiter.extention.UsersQueueExtension.StaticUser;

@WebTest
public class FriendsTest {

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(user.userName(), user.password())
                .goToFriendsList()
                .checkFriendExistsInList(user.friend());

    }
    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(user.userName(), user.password())
                .goToFriendsList()
                .checkNoFriendsInList();
    }
    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(user.userName(), user.password())
                .goToFriendsList()
                .checkFriendRequest(user.income());
    }
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(user.userName(), user.password())
                .goToAllPeopleList()
                .checkOutcomeRequestToUser(user.outcome());
    }
}
