package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.component.spend.SpendRow;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collector;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class SpendConditions {

    @Nonnull
    public static WebElementCondition spend(SpendJson expectedSpend) {
        return new WebElementCondition(expectedSpend.toString()) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final SpendJson actualSpend = SpendRow.toJson(element);
                return new CheckResult(
                        expectedSpend.toUI().equals(actualSpend),
                        actualSpend.toString()
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition spendRows(UserJson user) {
        return spendRows(user.spends());
    }

    @Nonnull
    public static WebElementsCondition spendRowsInAnyOrder(UserJson user) {
        return spendRowsInAnyOrder(user.spends());
    }
    @Nonnull
    public static WebElementsCondition spendRowsContains(UserJson user) {
        return spendRowsContains(user.spends());
    }

    @Nonnull
    public static WebElementsCondition spendRows(List<SpendJson> expectedSpends) {
        return SpendsTemplate(expectedSpends,toList(),Object::equals);
    }

    @Nonnull
    public static WebElementsCondition spendRowsInAnyOrder(List<SpendJson> expectedSpends) {
        return SpendsTemplate(expectedSpends,toSet(),Object::equals);
    }
    @Nonnull
    public static WebElementsCondition spendRowsContains(List<SpendJson> expectedSpends) {
        return SpendsTemplate(expectedSpends,toSet(),Collection::containsAll);
    }

    @Nonnull
    private static <T extends Collection<SpendJson>> WebElementsCondition SpendsTemplate(
            List<SpendJson> expectedSpends,
            Collector<SpendJson, ?, T> collector,
            BiPredicate<T, T> predicate) {
        return new WebElementsCondition() {

            private final T expectedElements = mapExpectedElements(collector, expectedSpends);

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
                            "Spends list mismatch (expected: %s, actual: %s)", expectedElements, actualElements
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
    private static <T extends Collection<SpendJson>> T mapExpectedElements(Collector<SpendJson, ?, T> collector, List<SpendJson> expectedSpends) {
        if (expectedSpends.isEmpty()) {
            throw new IllegalArgumentException("No expected spends given");
        }

        return expectedSpends.stream()
                .filter(s -> !s.category().archived())
                .map(SpendJson::toUI)
                .collect(collector);
    }

    @Nonnull
    private static <T extends Collection<SpendJson>> T mapActualElements(Collector<SpendJson, ?, T> collector, List<WebElement> elements) {
        return elements.stream()
                .map(SpendRow::toJson)
                .collect(collector);
    }
}
