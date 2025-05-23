package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.ScreenShootTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Test
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ScreenShootTestExtension.class)
public @interface ScreenShootTest {
    String value();
    boolean rewriteExpected() default false;
}
