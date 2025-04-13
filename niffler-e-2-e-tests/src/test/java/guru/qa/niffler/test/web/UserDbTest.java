package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.UserJson;
import guru.qa.niffler.db.service.UserDbService;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserDbTest {

    private final UserDbService userDbService = new UserDbService();

    @Test
    void userWillNotBeCreatedIfNameIsMissing() {
        UserJson userJson = genDefaultUser();
        userJson.setAccountNonExpired(null);

        Exception exception = assertThrows(RuntimeException.class, () -> userDbService.createUser(userJson));
        assertTrue(exception.getMessage().contains("org.postgresql.util.PSQLException: ERROR: null value in column " +
                "\"account_non_expired\" of relation \"user\" violates not-null constraint"));

        userJson.setAccountNonExpired(true);
        assertDoesNotThrow(() -> userDbService.createUser(userJson));
        userDbService.deleteUser(userJson);
    }

}
