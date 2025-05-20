package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.NonStaticBrowsersResolverExtension;
import guru.qa.niffler.utils.Browser;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(NonStaticBrowsersResolverExtension.class)
public @interface Browsers {
    Browser[] value() default {};
}
