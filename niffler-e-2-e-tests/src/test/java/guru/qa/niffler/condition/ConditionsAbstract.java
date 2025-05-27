package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collector;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public abstract class ConditionsAbstract<B extends Object> {

    private final Class<B> clazz;

    protected ConditionsAbstract(Class<B> clazz) {
        this.clazz = clazz;
    }

    

    @Nonnull
    private <T extends Collection<B>> WebElementsCondition template(
            List<B> expectedSpends,
            Collector<B, ?, T> collector,
            BiPredicate<T, T> predicate) {
        return new WebElementsCondition() {

            private final T expectedElements = mapExpectedElements(collector, expectedSpends);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                final BiPredicate<T, T> equalsPredicate = Object::equals;
                if (equalsPredicate == predicate && expectedElements.size() != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedElements.size(), elements.size());
                    return rejected(message, elements);
                }

                final T actualElements = mapActualElements(collector, elements);

                if (!predicate.test(actualElements,expectedElements)) {
                    final String message = String.format(
                            "%s list mismatch (expected: %s, actual: %s)", clazz.getSimpleName(), expectedElements, actualElements
                    );
                    return rejected(message, actualElements);
                }
                return accepted();
            }

            @NotNull
            @Override
            public String toString() {
                return expectedElements.toString();
            }
        };
    }

    @Nonnull
    protected abstract <T extends Collection<B>> T mapExpectedElements(Collector<B, ?, T> collector, List<B> expectedSpends);

    @Nonnull
    protected abstract  <T extends Collection<B>> T mapActualElements(Collector<B, ?, T> collector, List<WebElement> elements);
}
