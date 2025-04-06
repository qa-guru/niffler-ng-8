package guru.qa.niffler.test.web;

import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

public class FriendsPageTest extends BaseWebTest {

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        openFriendsPage(user)
                .checkFriendTableContainsName(user.friend());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        openFriendsPage(user)
                .checkEmptyFriends();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        openFriendsPage(user)
                .checkFriendRequestTableContainsName(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        openFriendsPage(user)
                .clickAllPeopleTab()
                .checkAllTableContainsOutcomeInvitationWithName(user.outcome());
    }

}