package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.BeforeEach;

import static guru.qa.niffler.utils.RandomDataUtils.randomPassword;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class BaseTest {
    protected static final Config CFG = Config.getInstance();

    protected String username;
    protected String password;

    @BeforeEach
    void setUp() {
        username = randomUsername();
        password = randomPassword();
    }
}
