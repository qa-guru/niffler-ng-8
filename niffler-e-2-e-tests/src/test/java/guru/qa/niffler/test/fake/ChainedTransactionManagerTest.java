package guru.qa.niffler.test.fake;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;
import guru.qa.niffler.service.impl.db.UserDbExperimentalService;
import guru.qa.niffler.service.impl.db.UserDbExperimentalService.Client;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;
import static org.junit.jupiter.api.Assertions.*;

/**
 * БЕЗ ТРАНИЗАКЦИЙ
 * Тестировать создание невальдного user`a без транзакции не имеет смысла, т.к. результат сразу понятен - в одной
 * БД данные будут созданы, а во второй нет.
 * Без транзакций создавать юзера есть смысл если мы будем соблюдать контракт.
 * <p>
 * С ТРАНЗАКИЯМИ
 * На мой взгляд, использовать JDBC или SpringJDBC не имеет большой разницы. Можно использовать любой понравившейся.
 * <p>
 * ChainedTransactionManager
 * НА сколько мне получилось разобраться, в среднем работает нормально, если ошибка не происходит при commit.
 * В данном случае ошибка происходит до commit`а, во время исполнения запроса, и данные остаются консистентными.
 * <p>
 * <p>
 * ТЕСТЫ:
 * 1) SpringJDBC + XA - Отсутствует username в Userdata
 * 2) SpringJDBC + XA - Отсутствует account_non_expired в AuthUser
 * 3) JDBC + XA - Отсутствует username в Userdata
 * 4) JDBC + XA - Отсутствует account_non_expired в AuthUser
 * 5) SpringJDBC + chained - Отсутствует username в Userdata
 * 6)SpringJDBC + chained - Отсутствует account_non_expired в AuthUser
 */
public class ChainedTransactionManagerTest {

    private final UserDbExperimentalService userDbExperimentalService = new UserDbExperimentalService();
    private final Client springJdbcClientWithSimpleDs = userDbExperimentalService.getSpringJdbcClientWithSimpleDs();
    private final Client springJdbcClientWithAtomikosDs = userDbExperimentalService.getSpringJdbcClientWithAtomikosDs();
    private final Client jbcClientWithWithAtomikosDs = userDbExperimentalService.getJbcClientWithWithAtomikosDs();
    private final XaTransactionTemplate xaTxTemplateWithAtomikosDs = userDbExperimentalService.getXaTxTemplateWithAtomikosDs();
    private final TransactionTemplate chainedTxTemplateWithSimpleDs = userDbExperimentalService.getChainedTxTemplateWithSimpleDs();

    @Test
    void springJdbcClientWithAtomikosDsAndTxIfUsernameInUserdataIsMissing() {
        UserParts userJson = genDefaultUser();
        System.out.println(userJson);
        UserdataUserJson user = userJson.getUserdataUserJson();
        String username = user.getUsername();
        user.setUsername(null);

        Exception exception = assertThrows(RuntimeException.class,
            () -> xaTxTemplateWithAtomikosDs.execute(() -> springJdbcClientWithAtomikosDs.createUser(userJson))
        );
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
            "\"username\" of relation \"user\" violates not-null constraint"));

        user.setUsername(username);
        assertDoesNotThrow(() -> xaTxTemplateWithAtomikosDs.execute(() -> springJdbcClientWithAtomikosDs.createUser(userJson)));
        xaTxTemplateWithAtomikosDs.execute(() -> springJdbcClientWithAtomikosDs.deleteUser(userJson));
    }

    @Test
    void springJdbcClientWithAtomikosDsAndTxIfAccountNonExpiredInAuthUserIsMissing() {
        UserParts userJson = genDefaultUser();
        System.out.println(userJson);
        AuthUserJson user = userJson.getAuthUserJson();
        user.setAccountNonExpired(null);

        Exception exception = assertThrows(RuntimeException.class,
            () -> xaTxTemplateWithAtomikosDs.execute(() -> springJdbcClientWithAtomikosDs.createUser(userJson))
        );
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
            "\"account_non_expired\" of relation \"user\" violates not-null constraint"));

        user.setAccountNonExpired(true);
        assertDoesNotThrow(() -> xaTxTemplateWithAtomikosDs.execute(() -> springJdbcClientWithAtomikosDs.createUser(userJson)));
        xaTxTemplateWithAtomikosDs.execute(() -> springJdbcClientWithAtomikosDs.deleteUser(userJson));
    }

    @Test
    void jbcClientWithWithAtomikosDsAndTxIfUsernameInUserdataIsMissing() {
        UserParts userJson = genDefaultUser();
        System.out.println(userJson);
        UserdataUserJson user = userJson.getUserdataUserJson();
        String username = user.getUsername();
        user.setUsername(null);

        Exception exception = assertThrows(RuntimeException.class,
            () -> xaTxTemplateWithAtomikosDs.execute(() -> jbcClientWithWithAtomikosDs.createUser(userJson))
        );
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
            "\"username\" of relation \"user\" violates not-null constraint"));

        user.setUsername(username);
        assertDoesNotThrow(() -> xaTxTemplateWithAtomikosDs.execute(() -> jbcClientWithWithAtomikosDs.createUser(userJson)));
        xaTxTemplateWithAtomikosDs.execute(() -> jbcClientWithWithAtomikosDs.deleteUser(userJson));
    }

    @Test
    void jbcClientWithWithAtomikosDsAndTxIfAccountNonExpiredInAuthUserIsMissing() {
        UserParts userJson = genDefaultUser();
        System.out.println(userJson);
        AuthUserJson user = userJson.getAuthUserJson();
        user.setAccountNonExpired(null);

        Exception exception = assertThrows(RuntimeException.class,
            () -> xaTxTemplateWithAtomikosDs.execute(() -> jbcClientWithWithAtomikosDs.createUser(userJson))
        );
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
            "\"account_non_expired\" of relation \"user\" violates not-null constraint"));

        user.setAccountNonExpired(true);
        assertDoesNotThrow(() -> xaTxTemplateWithAtomikosDs.execute(() -> jbcClientWithWithAtomikosDs.createUser(userJson)));
        xaTxTemplateWithAtomikosDs.execute(() -> jbcClientWithWithAtomikosDs.deleteUser(userJson));
    }

    @Test
    void springJdbcClientWithSimpleDsAndChainedTxIfUsernameInUserdataIsMissing() {
        UserParts userJson = genDefaultUser();
        System.out.println(userJson);
        UserdataUserJson user = userJson.getUserdataUserJson();
        String username = user.getUsername();
        user.setUsername(null);

        Exception exception = assertThrows(RuntimeException.class,
            () -> chainedTxTemplateWithSimpleDs.execute(status -> springJdbcClientWithSimpleDs.createUser(userJson))
        );
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
            "\"username\" of relation \"user\" violates not-null constraint"));

        user.setUsername(username);
        assertDoesNotThrow(() -> chainedTxTemplateWithSimpleDs.execute(status -> springJdbcClientWithSimpleDs.createUser(userJson)));
        chainedTxTemplateWithSimpleDs.execute(status -> {
            springJdbcClientWithSimpleDs.deleteUser(userJson);
            return null;
        });
    }

    @Test
    void springJdbcClientWithSimpleDsAndChainedTxIfAccountNonExpiredInAuthUserIsMissing() {
        UserParts userJson = genDefaultUser();
        System.out.println(userJson);
        AuthUserJson user = userJson.getAuthUserJson();
        user.setAccountNonExpired(null);

        Exception exception = assertThrows(RuntimeException.class,
            () -> chainedTxTemplateWithSimpleDs.execute(status -> springJdbcClientWithSimpleDs.createUser(userJson))
        );
        assertTrue(exception.getMessage().contains("ERROR: null value in column " +
            "\"account_non_expired\" of relation \"user\" violates not-null constraint"));

        user.setAccountNonExpired(true);
        assertDoesNotThrow(() -> chainedTxTemplateWithSimpleDs.execute(status -> springJdbcClientWithSimpleDs.createUser(userJson)));
        chainedTxTemplateWithSimpleDs.execute(status -> {
            springJdbcClientWithSimpleDs.deleteUser(userJson);
            return null;
        });
    }
}
