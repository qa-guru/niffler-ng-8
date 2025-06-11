package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.PageOpenUtil.open;

@WebTest
public class FriendsWebTest {

    @User(friends = 1)
    @Test
    @ApiLogin
    void friendShouldBePresentInFriendsTable(UserJson user) {
        open(FriendsPage.class)
                .assertFriendExist(user);
    }

    @User
    @Test
    @ApiLogin
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        open(FriendsPage.class)
                .assertEmptyUser();
    }

    @User(incomeInvitations = 1)
    @Test
    @ApiLogin
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        open(FriendsPage.class)
                .assertFriendRequestExist(user);
    }
    @User(outcomeInvitations = 1)
    @Test
    @ApiLogin
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        open(AllPeoplePage.class)
                .assertOutcomeInvitations(user);
    }

    @User(incomeInvitations = 1)
    @Test
    @ApiLogin
    void acceptFriendRequest(UserJson user) {
        open(FriendsPage.class)
                .assertFriendRequestExist(user)
                .acceptFriend(user.testData().incomeInvitations().getFirst());
    }

    @User(incomeInvitations = 1)
    @Test
    @ApiLogin
    void declineFriendRequest(UserJson user) {
        open(FriendsPage.class)
                .assertFriendRequestExist(user)
                .declineFriend(user.testData().incomeInvitations().getFirst());
    }
}
