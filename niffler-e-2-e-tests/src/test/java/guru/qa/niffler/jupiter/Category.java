package guru.qa.niffler.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(CategoryExtension.class)
public @interface Category {
    String username();
    boolean archived();
}
