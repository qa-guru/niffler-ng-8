package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.extensions.CategoryExtension;
import guru.qa.niffler.jupiter.extensions.CreateSpendingExtension;
import guru.qa.niffler.jupiter.extensions.SpendingResolverExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({CategoryExtension.class, CreateSpendingExtension.class, SpendingResolverExtension.class})
public @interface User {
    String username();
    Category[] categories() default  {};
    Spending[] spendings() default  {};
}
