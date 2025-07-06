package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

public class FriendsPageTest extends BaseWebTest {

    @Test
    @User(withFriend = 1)
    @ApiLogin
    void friendShouldBePresentInFriendsTable(UserParts user) {
        String friendName = user.getTestData().getFriendsNames().getFirst();
        openFriendsPage()
            .checkFriendTableContainsName(friendName);
    }

    @Test
    @User
    @ApiLogin
    void friendsTableShouldBeEmptyForNewUser(UserParts user) {
        openFriendsPage()
            .checkEmptyFriends();
    }

    @Test
    @User(withInInvite = 1)
    @ApiLogin
    void incomeInvitationBePresentInFriendsTable(UserParts user) {
        openFriendsPage()
            .checkFriendRequestTableContainsName(user.getTestData().getInInviteNames().getFirst());
    }

    @Test
    @User(withOutInvite = 1)
    @ApiLogin
    void outcomeInvitationBePresentInAllPeoplesTable(UserParts user) {
        String friendName = user.getTestData().getOutInviteNames().getFirst();
        openFriendsPage()
            .clickAllPeopleTab()
            .findUsername(friendName)
            .checkAllTableContainsOutcomeInvitationWithName(friendName);
    }

    @Test
    @User(withInInvite = 1)
    @ApiLogin
    void acceptIncomeInvitation(UserParts user) {
        String incomeUsername = user.getTestData().getInInviteNames().getFirst();
        openFriendsPage()
            .clickAcceptBtnForName(incomeUsername)
            .checkFriendTableContainsName(incomeUsername);
    }

    @Test
    @User(withInInvite = 1)
    @ApiLogin
    void declineIncomeInvitation(UserParts user) {
        String incomeUsername = user.getTestData().getInInviteNames().getFirst();
        openFriendsPage()
            .clickDeclineBtnForName(incomeUsername)
            .getModal().clickAcceptBtn().returnToPage()
            .checkEmptyFriends();
    }
}