package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.service.UserDbService;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserDbTest {

    private final UserDbService userDbService = new UserDbService();

    @Test
    void userWillNotBeCreatedIfUsernameIsMissing() {
        UserParts userJson = genDefaultUser();
        UserdataUserJson authUser = userJson.userdataUserJson();
        String username = authUser.getUsername();
        authUser.setUsername(null);

        Exception exception = assertThrows(RuntimeException.class, () -> userDbService.createUser(userJson));
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
                "\"username\" of relation \"user\" violates not-null constraint"));

        authUser.setUsername(username);
        assertDoesNotThrow(() -> userDbService.createUser(userJson));
        userDbService.deleteUser(userJson);
    }

}
