package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collector;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static guru.qa.niffler.model.Bubble.Actual;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition statBubble(Bubble expectedBubble) {
        return new WebElementCondition(expectedBubble.expected()) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final Actual actual = new Actual(element);
                return new CheckResult(
                        expectedBubble.equals(actual),
                        actual.actual()
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubbles(List<Bubble> expectedBubbles) {
        return statBubblesTemplate(expectedBubbles,toList(),Object::equals);
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(List<Bubble> expectedBubbles) {
        return statBubblesTemplate(expectedBubbles,toSet(),Object::equals);
    }
    @Nonnull
    public static WebElementsCondition statBubblesContains(List<Bubble> expectedBubbles) {
        return statBubblesTemplate(expectedBubbles,toSet(),Collection::containsAll);
    }

    @Nonnull
    public static WebElementsCondition statBubbles(Bubble... expected) {
        return statBubbles(List.of(expected));
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expected) {
        return statBubblesInAnyOrder(List.of(expected));
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(Bubble... expected) {
        return statBubblesContains(List.of(expected));
    }


    @Nonnull
    private static <T extends Collection<String>> T mapExpectedElements(Collector<String, ?, T> collector, List<Bubble> expected) {
        if (expected.isEmpty()) {
            throw new IllegalArgumentException("No expected bubbles given");
        }

        return expected.stream()
                .map(Bubble::expected)
                .collect(collector);
    }

    @Nonnull
    private static <T extends Collection<String>> T mapActualElements(Collector<String, ?, T> collector, List<WebElement> elements) {
        return elements.stream()
                .map(e -> new Actual(e).actual())
                .collect(collector);
    }

    @Nonnull
    private static <T extends Collection<String>> WebElementsCondition statBubblesTemplate(
            List<Bubble> expectedBubbles,
            Collector<String, ?, T> collector,
            BiPredicate<T, T> predicate) {
        return new WebElementsCondition() {

            private final T expectedElements = mapExpectedElements(collector, expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                final BiPredicate<T, T> equalsPredicate = Object::equals;
                if (predicate == equalsPredicate && expectedElements.size() != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedElements.size(), elements.size());
                    return rejected(message, elements);
                }

                final T actualElements = mapActualElements(collector, elements);

                if (!predicate.test(actualElements,expectedElements)) {
                    final String message = String.format(
                            "Bubbles list mismatch (expected: %s, actual: %s)", expectedElements, actualElements
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
}