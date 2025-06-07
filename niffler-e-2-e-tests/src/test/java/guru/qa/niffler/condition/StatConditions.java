package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.condition.model.Bubble;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition bubble(guru.qa.niffler.condition.model.Bubble expectedBubble) {
        return new WebElementCondition("color " + expectedBubble) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String actualRgba = element.getCssValue("background-color");
                final String actualText = element.getText();

                boolean colorMatches = expectedBubble.color().rgb.equals(actualRgba);
                boolean textMatches = expectedBubble.text().equals(actualText);

                if (colorMatches && textMatches) {
                    return accepted();
                } else {
                    String reason = String.format(
                            "Mismatch: color %s (expected: %s), text '%s' (expected: '%s')",
                            actualRgba, expectedBubble.color().rgb,
                            actualText, expectedBubble.text()
                    );
                    return rejected(reason, actualRgba + "; " + actualText);
                }
            }
        };
    }

    @Nonnull
    public static WebElementsCondition bubbles(guru.qa.niffler.condition.model.Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final String expectedBubblesStr = Arrays.toString(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    String message = String.format(
                            "List size mismatch (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size()
                    );
                    return rejected(message, elements);
                }

                List<String> actualBubbleList = new ArrayList<>();
                List<String> mismatches = new ArrayList<>();

                for (int i = 0; i < elements.size(); i++) {
                    WebElement element = elements.get(i);
                    guru.qa.niffler.condition.model.Bubble expected = expectedBubbles[i];

                    String actualRgba = element.getCssValue("background-color");
                    String actualText = element.getText();
                    actualBubbleList.add("color: " + actualRgba + ", text: '" + actualText + "'");

                    boolean colorMatches = expected.color().rgb.equals(actualRgba);
                    boolean textMatches = expected.text().equals(actualText);

                    if (!colorMatches || !textMatches) {
                        List<String> errors = new ArrayList<>();
                        if (!colorMatches) {
                            errors.add("color: " + actualRgba + " (expected: " + expected.color().rgb + ")");
                        }
                        if (!textMatches) {
                            errors.add("text: '" + actualText + "' (expected: '" + expected.text() + "')");
                        }
                        mismatches.add(i + ": " + String.join(", ", errors));
                    }
                }

                if (mismatches.isEmpty()) {
                    return accepted();
                } else {
                    String reason = "Mismatches at indices: " + String.join(", ", mismatches);
                    return rejected(reason, actualBubbleList);
                }
            }

            @NotNull
            @Override
            public String toString() {
                return "bubbles: " + expectedBubblesStr;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(guru.qa.niffler.condition.model.Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final String expectedBubblesStr = Arrays.toString(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    String message = String.format(
                            "List size mismatch (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size()
                    );
                    return rejected(message, elements);
                }

                Map<String, Long> expectedCounts = Arrays.stream(expectedBubbles)
                        .collect(Collectors.groupingBy(
                                b -> b.color().rgb + "|" + b.text(),
                                Collectors.counting()
                        ));

                List<Bubble> actualBubbles = new ArrayList<>();
                Map<String, Long> actualCounts = new HashMap<>();

                for (WebElement element : elements) {
                    String color = element.getCssValue("background-color");
                    String text = element.getText();
                    String key = color + "|" + text;

                    actualBubbles.add(new Bubble(Color.fromRgb(color), text));
                    actualCounts.put(key, actualCounts.getOrDefault(key, 0L) + 1);
                }

                Set<String> missing = new HashSet<>();
                Set<String> unexpected = new HashSet<>();
                Set<String> commonKeys = new HashSet<>(expectedCounts.keySet());
                commonKeys.addAll(actualCounts.keySet());

                for (String key : commonKeys) {
                    long expectedCount = expectedCounts.getOrDefault(key, 0L);
                    long actualCount = actualCounts.getOrDefault(key, 0L);

                    if (expectedCount > actualCount) {
                        missing.add(key + " (count: " + (expectedCount - actualCount) + ")");
                    }
                    if (actualCount > expectedCount) {
                        unexpected.add(key + " (count: " + (actualCount - expectedCount) + ")");
                    }
                }

                if (missing.isEmpty() && unexpected.isEmpty()) {
                    return accepted();
                }

                String message = "Bubble mismatch\n";
                if (!missing.isEmpty()) {
                    message += "Missing: " + missing + "\n";
                }
                if (!unexpected.isEmpty()) {
                    message += "Unexpected: " + unexpected + "\n";
                }
                message += "Expected: " + Arrays.toString(expectedBubbles) + "\n";
                message += "Actual  : " + actualBubbles;

                return rejected(message, actualBubbles);
            }

            @NotNull
            @Override
            public String toString() {
                return "statBubblesInAnyOrder " + expectedBubblesStr;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(guru.qa.niffler.condition.model.Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubblesStr = Arrays.toString(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    return accepted();
                }

                List<Bubble> actualBubbles = new ArrayList<>();
                for (WebElement element : elements) {
                    String color = element.getCssValue("background-color");
                    String text = element.getText();
                    actualBubbles.add(new Bubble(Color.fromRgb(color) , text));
                }

                List<Bubble> expectedToFind = Arrays.stream(expectedBubbles)
                        .map(b -> new Bubble(Color.fromRgb(b.color().rgb), b.text()))
                        .toList();

                List<Bubble> missing = getBubbleRepresentations(expectedToFind, actualBubbles);

                if (missing.isEmpty()) {
                    return accepted();
                } else {
                    String message = String.format(
                            "Missing %d bubbles: %s\nActual bubbles: %s",
                            missing.size(), missing, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
            }

            @NotNull
            private List<Bubble> getBubbleRepresentations(List<Bubble> expectedToFind, List<Bubble> actualBubbles) {
                List<Bubble> missing = new ArrayList<>();
                for (Bubble expected : expectedToFind) {
                    boolean found = false;
                    for (Bubble actual : actualBubbles) {
                        if (expected.equals(actual)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missing.add(expected);
                    }
                }
                return missing;
            }

            @NotNull
            @Override
            public String toString() {
                return "statBubblesContains " + expectedBubblesStr;
            }
        };
    }
}
