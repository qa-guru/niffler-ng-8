package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.config.Browser;
import guru.qa.niffler.jupiter.annotation.ConvertBrowser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.NonStaticBrowsersExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static guru.qa.niffler.util.RandomDataUtils.genPassword;

public class LoginPageTest extends BaseWebTest {

    public static final SelenideConfig Sc = new SelenideConfig()
        .browser("chrome")
        .pageLoadStrategy("eager")
        .timeout(5000L);

    @RegisterExtension
    private static final NonStaticBrowsersExtension browserExtension = new NonStaticBrowsersExtension();

    @Test
    @User(username = "user")
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserParts user) {
        SelenideDriver selenideDriver = new SelenideDriver(Sc);
        browserExtension.drivers().add(selenideDriver);
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
            .checkMainPage();
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User
    void userShouldStayOnLoginPageAfterLoginWithBadCredential(@ConvertBrowser SelenideDriver driver, UserParts user) {
        String errPassword = genPassword();
        openLoginPage(driver)
            .doLoginError(user.getUsername(), errPassword)
            .checkBadCredentialsError();
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User
    void userShouldStayOnLoginPageAfterLoginWithBadCredential(Browser browser, UserParts user) {
        ExecutorService es = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(es.submit(() -> {
                SelenideDriver driver = new SelenideDriver(browser.getConfig());
                try {
                    String errPassword = genPassword();
                    openLoginPage(driver)
                        .doLoginError(user.getUsername(), errPassword)
                        .checkBadCredentialsError();
                } finally {
                    driver.close();
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new AssertionError("Параллельный тест упал", e.getCause());
            }
        }
        es.shutdown();
    }
}
