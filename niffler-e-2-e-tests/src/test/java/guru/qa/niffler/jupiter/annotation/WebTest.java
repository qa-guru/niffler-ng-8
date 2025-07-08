package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
    IssueExtension.class,
    BrowserExtension.class,
    AllureJunit5.class,
    UserExtension.class,
    CategoryExtension.class,
    SpendingExtension.class,
    ApiLoginExtension.class,
    ScreenShotTestExtension.class
})
public @interface WebTest {
}
