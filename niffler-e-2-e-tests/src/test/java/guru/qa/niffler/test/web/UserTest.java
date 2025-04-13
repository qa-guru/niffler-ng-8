package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extention.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtension.class)
public class UserTest {

    @Test
    @DisabledByIssue("2")
    void testWithEmptyUser0(@UserType() UsersQueueExtension.StaticUser user) throws InterruptedException {
        Thread.sleep(1000);
        System.err.println(user);
    }

}
