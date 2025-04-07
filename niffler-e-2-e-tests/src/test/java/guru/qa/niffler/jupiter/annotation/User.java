package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import guru.qa.niffler.jupiter.extension.UserResolver;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static guru.qa.niffler.jupiter.annotation.User.Mode.DEFAULT;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({UserResolver.class, CategoryExtension.class, SpendingExtension.class})
public @interface User {

    Category[] categories() default {};

    Spending[] spendings() default {};

    Mode value() default DEFAULT;

    enum Mode {
        DEFAULT, GEN
    }

}
