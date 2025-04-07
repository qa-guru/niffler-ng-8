package guru.qa.niffler.jupiter.annotations;

public @interface User {
    Category[] categories() default  {};
    Spending[] spendings() default  {};
}
