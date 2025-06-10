package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UsersApiClient;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedTests {


    @User
    @Test
    @Order(1)
    void firstUserTest(UserJson user) {
        List<UserJson> userList = new UsersApiClient().allUsers(user.username(), null);
        Assertions.assertFalse(userList.isEmpty(), "Список пользователей должен быть пустым");
    }

    @User
    @Test
    @Order(Integer.MAX_VALUE)
    void lastTest(UserJson user) {
        List<UserJson> userList = new UsersApiClient().allUsers(user.username(), null);
        Assertions.assertFalse(userList.isEmpty(), "Список пользователей должен быть не пустым");
    }
}
