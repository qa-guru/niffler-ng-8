package guru.qa.niffler.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static guru.qa.niffler.jupiter.UseUser.Mode.NON;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(GenCategoryExtension.class)
public @interface GenCategory {
    UseUser user() default @UseUser(value = NON);
    boolean archived();
}
