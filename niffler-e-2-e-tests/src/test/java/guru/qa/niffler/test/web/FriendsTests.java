package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotations.UserType;
import guru.qa.niffler.jupiter.extensions.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class FriendsTests {
    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendShouldBePresentFriendsTable(@UserType UsersQueueExtension.StaticUser user1,
                                           @UserType UsersQueueExtension.StaticUser user2) {

    }
}
