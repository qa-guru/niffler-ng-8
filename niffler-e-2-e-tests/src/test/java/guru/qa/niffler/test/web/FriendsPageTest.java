package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

public class FriendsPageTest extends BaseWebTest {

    @Test
    @User(withFriend = 1)
    void friendShouldBePresentInFriendsTable(UserParts user) {
        String friendName = user.getTestData().getFriendsNames().getFirst();
        openFriendsPage(user)
            .checkFriendTableContainsName(friendName);
    }

    @Test
    @User
    void friendsTableShouldBeEmptyForNewUser(UserParts user) {
        openFriendsPage(user)
            .checkEmptyFriends();
    }

    @Test
    @User(withInInvite = 1)
    void incomeInvitationBePresentInFriendsTable(UserParts user) {
        openFriendsPage(user)
            .checkFriendRequestTableContainsName(user.getTestData().getInInviteNames().getFirst());
    }

    @Test
    @User(withOutInvite = 1)
    void outcomeInvitationBePresentInAllPeoplesTable(UserParts user) {
        String friendName = user.getTestData().getOutInviteNames().getFirst();
        openFriendsPage(user)
            .clickAllPeopleTab()
            .findUsername(friendName)
            .checkAllTableContainsOutcomeInvitationWithName(friendName);
    }

    @Test
    @User(withInInvite = 1)
    void acceptIncomeInvitation(UserParts user) {
        String incomeUsername = user.getTestData().getInInviteNames().getFirst();
        openFriendsPage(user)
            .clickAcceptBtnForName(incomeUsername)
            .checkFriendTableContainsName(incomeUsername);
    }

    @Test
    @User(withInInvite = 1)
    void declineIncomeInvitation(UserParts user) {
        String incomeUsername = user.getTestData().getInInviteNames().getFirst();
        openFriendsPage(user)
            .clickDeclineBtnForName(incomeUsername)
            .getAlert().clickAcceptBtn().returnToPage()
            .checkEmptyFriends();
    }
}