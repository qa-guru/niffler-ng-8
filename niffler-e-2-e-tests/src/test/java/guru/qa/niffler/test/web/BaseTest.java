package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.BeforeEach;

import static guru.qa.niffler.data.UserTestData.randomPassword;
import static guru.qa.niffler.data.UserTestData.rndUsername;

public class BaseTest {
    protected static final Config CFG = Config.getInstance();

    protected String username;
    protected String password;

    @BeforeEach
    void setUp() {
        username = rndUsername();
        password = randomPassword();
    }
}
