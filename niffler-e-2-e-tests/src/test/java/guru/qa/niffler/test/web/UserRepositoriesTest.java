package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.db.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.db.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.db.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.db.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.db.repository.impl.spring_jdbc.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.db.repository.impl.spring_jdbc.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.db.service.UserClient;
import guru.qa.niffler.db.service.impl.UserDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;
import static guru.qa.niffler.util.RandomDataUtils.genUsername;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class UserRepositoriesTest {

    static Stream<UserClient> provider() {
        return Stream.of(
            new UserDbClient(new AuthUserRepositoryHibernate(), new UserdataUserRepositoryHibernate()),
            new UserDbClient(new AuthUserRepositorySpringJdbc(), new UserdataUserRepositorySpringJdbc()),
            new UserDbClient(new AuthUserRepositoryJdbc(), new UserdataUserRepositoryJdbc())
        );
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testFindByUsername(UserClient userClient) {
        UserParts userJson = genDefaultUser();

        UserParts createdUser = userClient.createUser(userJson);
        userClient.createFriends(createdUser, 1);

        Optional<UserParts> fetched = userClient.findByUsername(userJson.getUsername());
        assertTrue(fetched.isPresent());
        assertEquals(userJson.getUsername(), fetched.get().getUsername());

        userClient.deleteUser(createdUser);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testFindById(UserClient userClient) {
        UserParts userJson = genDefaultUser();

        UserParts createdUser = userClient.createUser(userJson);
        userClient.createIncomeInvitation(createdUser, 1);

        Optional<UserParts> fetched = userClient.findByAuthId(createdUser.getAuthId());
        assertTrue(fetched.isPresent());
        assertEquals(userJson.getUsername(), fetched.get().getUsername());

        userClient.deleteUser(createdUser);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testUpdate(UserClient userClient) {
        UserParts userJson = genDefaultUser();

        UserParts createdUser = userClient.createUser(userJson);
        userClient.createOutcomeInvitation(createdUser, 1);

        String newUsername = genUsername();
        createdUser.setUsername(newUsername);
        userClient.updateUser(createdUser);

        Optional<UserParts> fetched = userClient.findByAuthId(createdUser.getAuthId());
        assertTrue(fetched.isPresent());
        assertEquals(newUsername, fetched.get().getUsername());

        userClient.deleteUser(createdUser);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testDelete(UserClient userClient) {
        UserParts userJson = genDefaultUser();

        UserParts createdUser = userClient.createUser(userJson);

        userClient.deleteUser(userJson);

        Optional<UserParts> fetched = userClient.findByAuthId(createdUser.getAuthId());
        assertFalse(fetched.isPresent());
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testFindAll(UserClient userClient) {
        UserParts userJson1 = genDefaultUser();
        UserParts userJson2 = genDefaultUser();

        userClient.createUser(userJson1);
        userClient.createUser(userJson2);

        List<UserParts> allUsers = userClient.findAll();
        assertTrue(allUsers.size() >= 2);

        userClient.deleteUser(userJson1);
        userClient.deleteUser(userJson2);
    }

}
