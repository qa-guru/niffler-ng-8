package guru.qa.niffler.jupiter.users;

import guru.qa.niffler.jupiter.category.Category;
import guru.qa.niffler.jupiter.category.CategoryExtension;
import guru.qa.niffler.jupiter.spending.Spend;
import guru.qa.niffler.jupiter.spending.SpendingExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({CategoryExtension.class, SpendingExtension.class})
public @interface User {

    String username() default "";

    Category[] categories() default {};

    Spend[] spendings() default {};

}
