package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.db.service.UserDbService;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserDbTest {

    private final UserDbService userDbService = new UserDbService();

    @Test
    void userWillNotBeCreatedIfAccountNonExpiredIsMissing() {
        UserParts userJson = genDefaultUser();
        AuthUserJson authUser = userJson.authUserJson();
        authUser.setAccountNonExpired(null);

        Exception exception = assertThrows(RuntimeException.class, () -> userDbService.createUser(userJson));
        assertTrue(exception.getMessage().contains("org.postgresql.util.PSQLException: ERROR: null value in column " +
                "\"account_non_expired\" of relation \"user\" violates not-null constraint"));
        authUser.setAccountNonExpired(true);
        assertDoesNotThrow(() -> userDbService.createUser(userJson));
        userDbService.deleteUser(userJson);
    }

}
