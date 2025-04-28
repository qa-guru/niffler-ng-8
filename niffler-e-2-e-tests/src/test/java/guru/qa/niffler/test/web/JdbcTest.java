package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.jdbc.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.SpendRepositorySpringJdbc;
import guru.qa.niffler.model.*;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.db.dao.SpendDaoDbClient;
import guru.qa.niffler.service.db.dao.UsersDaoDbClient;
import guru.qa.niffler.service.db.dao.jdbc.UsersDaoJdbcClient;
import guru.qa.niffler.service.db.dao.spring.SpendDaoSpringClient;
import guru.qa.niffler.service.db.dao.spring.UsersDaoSpringClient;
import guru.qa.niffler.service.db.repository.UserRepositoryDbClient;
import guru.qa.niffler.service.db.repository.jdbc.UsersRepositoryJdbcClient;
import guru.qa.niffler.service.db.repository.spring.UsersRepositorySpringClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;


public class JdbcTest {

    private static final String PASSWORD = "12345";

    static UsersClient[] provideUserClients() {
        return new UsersClient[] {
                new UsersDaoJdbcClient(),
                new UsersDaoSpringClient(),
                new UsersRepositoryJdbcClient(),
                new UsersRepositorySpringClient()
        };
    }

    static UsersDaoDbClient[] provideUserDaoClients() {
        return new UsersDaoDbClient[] {
                new UsersDaoJdbcClient(),
                new UsersDaoSpringClient()
        };
    }

    static UserRepositoryDbClient[] provideUserRepositoryClients() {
        return new UserRepositoryDbClient[] {
                new UsersRepositoryJdbcClient(),
                new UsersRepositorySpringClient()
        };
    }

    static SpendRepository[] provideSpendRepository() {
        return new SpendRepository[] {
                new SpendRepositoryJdbc(),
                new SpendRepositorySpringJdbc()
        };
    }

    @ParameterizedTest
    @MethodSource("provideUserClients")
    void xaCreateUserTest(UsersClient usersClient){
        UserJson user = usersClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        System.out.println(user);
    }

    @ParameterizedTest
    @MethodSource("provideUserDaoClients")
    void createUserJdbcTest(UsersDaoDbClient usersClient) {
        UserJson user = usersClient.notXaCreateUser(
                        RandomDataUtils.randomUsername(),
                        PASSWORD
        );
        System.out.println(user);
    }


    @Test
    void createUserChainedTxManagerTest() {
        UsersDaoSpringClient authUsersDbClient = new UsersDaoSpringClient();
        UserJson user = authUsersDbClient.createUserChainedTxManager(
                        RandomDataUtils.randomUsername(),
                        PASSWORD
        );
        System.out.println(user);
    }

    @ParameterizedTest
    @MethodSource("provideUserRepositoryClients")
    void createUserWithFriendTest(UserRepositoryDbClient userClient) {
        UserJson user = userClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        userClient.addFriend(user,1);
        System.out.println(user);
    }

    @ParameterizedTest
    @MethodSource("provideUserRepositoryClients")
    void createUserWithOutcomeRequestTest(UserRepositoryDbClient userClient) {
        UserJson requester = userClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        userClient.addOutcomeInvitation(requester, 1);
        System.out.println(requester);
    }

    @ParameterizedTest
    @MethodSource("provideUserRepositoryClients")
    void createUserWithIncomeRequestTest(UserRepositoryDbClient userClient) {
        UserJson requester = userClient.createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        userClient.addIncomeInvitation(requester, 1);
        System.out.println(requester);
    }

    @ParameterizedTest
    @MethodSource("provideSpendRepository")
    void spendRepositoryJdbcTest(SpendRepository spendRepository) {
        UserJson user = new UsersRepositorySpringClient().createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );

        SpendEntity spend = SpendEntity.fromJson(
                SpendJson.spendJson(
                        user.username(),
                        RandomDataUtils.randomCategoryName()
                )
        );

        SpendEntity spendEntity = spendRepository.create(spend);

        Optional<SpendEntity> spendById = spendRepository.findSpendById(spendEntity.getId());
        SpendJson spendJsonById = SpendJson.fromEntity(spendById.orElseThrow());
        System.out.println(spendJsonById);
    }

    @Test
    void createSpendSpringTest() {
        UserJson user = new UsersRepositorySpringClient().createUser(
                RandomDataUtils.randomUsername(),
                PASSWORD
        );
        SpendDaoDbClient spendDbClient = new SpendDaoSpringClient();
        SpendJson spendJson = spendDbClient.createSpendNotAcid(
                SpendJson.spendJson(
                        user.username(),
                        RandomDataUtils.randomCategoryName())
        );
        System.out.println(spendJson);
    }
}