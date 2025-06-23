package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.component.spend.SpendRow;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collector;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ParametersAreNonnullByDefault
public class SpendConditions {


    public static WebElementCondition spend(SpendJson expectedSpend) {
        return new WebElementCondition(expectedSpend.toString()) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final SpendJson actualSpend = SpendRow.toJson(element, driver);
                return new CheckResult(
                        expectedSpend.toUI().equals(actualSpend),
                        actualSpend.toString()
                );
            }
        };
    }
    
    public static WebElementsCondition spendRows(UserJson user) {
        return spendRows(user.spends());
    }
    
    public static WebElementsCondition spendRowsInAnyOrder(UserJson user) {
        return spendRowsInAnyOrder(user.spends());
    }
    
    public static WebElementsCondition spendRowsContains(UserJson user) {
        return spendRowsContains(user.spends());
    }
    
    public static WebElementsCondition spendRows(List<SpendJson> expectedSpends) {
        return spendsTemplate(expectedSpends,toList(),Object::equals);
    }
    
    public static WebElementsCondition spendRowsInAnyOrder(List<SpendJson> expectedSpends) {
        return spendsTemplate(expectedSpends,toSet(),Object::equals);
    }

    public static WebElementsCondition spendRowsContains(List<SpendJson> expectedSpends) {
        return spendsTemplate(expectedSpends,toSet(),Collection::containsAll);
    }

    private static <T extends Collection<SpendJson>> WebElementsCondition spendsTemplate(
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

                final T actualElements = mapActualElements(collector, elements,driver);

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

    private static <T extends Collection<SpendJson>> T mapExpectedElements(Collector<SpendJson, ?, T> collector, List<SpendJson> expectedSpends) {
        if (expectedSpends.isEmpty()) {
            throw new IllegalArgumentException("No expected spends given");
        }

        return expectedSpends.stream()
                .filter(s -> !s.category().archived())
                .map(SpendJson::toUI)
                .collect(collector);
    }

    private static <T extends Collection<SpendJson>> T mapActualElements(
            Collector<SpendJson, ?, T> collector,
            List<WebElement> elements,
            Driver driver) {
        return elements.stream()
                .map(we -> SpendRow.toJson(we,driver))
                .collect(collector);
    }
}
