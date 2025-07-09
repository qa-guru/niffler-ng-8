package guru.qa.niffler.data.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.rejected;

public class StatConditions {

    public static WebElementCondition color(Color expectedColor) {

        return new WebElementCondition("color") {
            @Override
            public CheckResult check(Driver driver, WebElement webElement) {
                final String rgba = webElement.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition color(@Nonnull Color... expectedColors) {

        return new WebElementsCondition() {

            private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();


            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("Expected colors not provided");
                }
                if (elements.size() != expectedColors.length) {
                    throw new IllegalArgumentException("Expected " + expectedColors.length + " colors but got " + elements.size());
                }

                boolean found = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);

                    if (found) {
                        found = colorToCheck.rgb.equals(rgba);
                    }
                }

                if (!found) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format("List colors mismatch (Expected: %s, Actual: %s", expectedColors.length, actualRgba);

                    return rejected(message, actualRgba);
                }

                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    public static WebElementsCondition shouldHaveBubbles(@Nonnull Bubble... expectedBubbles) {

        return new WebElementsCondition() {

            private final String[] expectedColors = Arrays.stream(expectedBubbles).map(bubble -> bubble.color().rgb).toArray(String[]::new);
            private final String[] expectedTexts = Arrays.stream(expectedBubbles).map(Bubble::text).toArray(String[]::new);


            @Override
            public CheckResult check(@Nonnull Driver driver,@Nonnull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("Expected bubbles not provided");
                }
                if (elements.size() != expectedBubbles.length) {
                    throw new IllegalArgumentException("Expected " + expectedBubbles.length + " colors but got " + elements.size());
                }

                boolean found = true;
                boolean textExist = true;
                List<String> actualRgbaList = new ArrayList<>();
                List<String> actualTextList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final String colorToCheck = expectedBubbles[i].color().rgb;
                    final String stringToCheck = expectedBubbles[i].text();
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualRgbaList.add(rgba);
                    actualTextList.add(text);

                    if (found) {
                        found = colorToCheck.equals(rgba);
                        if (textExist){
                            textExist = stringToCheck.equals(text);
                        }
                        if (!textExist) {
                            final String actualTexts = actualTextList.toString();
                            final String message = String.format("List spendings mismatch (Expected: %s, Actual: %s", expectedBubbles[i].text(), actualTexts);

                            throw new AssertionError(message);
                        }
                    }
                }

                if (!found) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format("List colors mismatch (Expected: %s, Actual: %s", expectedBubbles[0].color(), actualRgba);

                    return rejected(message, actualRgba);
                }

                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return "Text: " + Arrays.toString(expectedTexts) +
                        "\nColor:" + Arrays.toString(expectedColors);
            }
        };
    }

    public static WebElementsCondition shouldHaveStatBubblesInAnyOrder(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final Map<String, String> expectedBubblesMap = Arrays.stream(expectedBubbles)
                    .collect(Collectors.toMap(bubble -> bubble.color().rgb, Bubble::text));

            @Override
            public CheckResult check(@Nonnull Driver driver, @Nonnull List<WebElement> elements) {
                if (expectedBubblesMap.isEmpty()) {
                    throw new IllegalArgumentException("Expected bubbles not provided");
                }

                if (elements.size() != expectedBubblesMap.size()) {
                    throw new IllegalArgumentException("Expected " + expectedBubblesMap.size() + " bubbles but got " + elements.size());
                }

                Set<Map.Entry<String, String>> actualBubblesSet = new HashSet<>();

                for (WebElement element : elements) {
                    String rgba = element.getCssValue("background-color");
                    String text = element.getText();
                    actualBubblesSet.add(new AbstractMap.SimpleEntry<>(rgba, text));
                }

                for (Map.Entry<String, String> expectedBubble : expectedBubblesMap.entrySet()) {
                    if (!actualBubblesSet.contains(expectedBubble)) {
                        final String message = String.format("Expected bubble with color %s and text '%s' not found",
                                expectedBubble.getKey(), expectedBubble.getValue());
                        return rejected(message, actualBubblesSet.toString());
                    }
                }

                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return "Expected Bubbles: " + expectedBubblesMap.entrySet().stream()
                        .map(entry -> "Color: " + entry.getKey() + ", Text: " + entry.getValue())
                        .collect(Collectors.joining(", "));
            }
        };
    }

    public static WebElementsCondition shouldContainsStatBubbles(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final Map<String, String> expectedBubblesMap = Arrays.stream(expectedBubbles)
                    .collect(Collectors.toMap(bubble -> bubble.color().rgb, Bubble::text));

            @Override
            public CheckResult check(@Nonnull Driver driver, @Nonnull List<WebElement> elements) {
                if (expectedBubblesMap.isEmpty()) {
                    throw new IllegalArgumentException("Expected bubbles not provided");
                }

                Set<Map.Entry<String, String>> actualBubblesSet = new HashSet<>();

                for (WebElement element : elements) {
                    String rgba = element.getCssValue("background-color");
                    String text = element.getText();
                    actualBubblesSet.add(new AbstractMap.SimpleEntry<>(rgba, text));
                }

                for (Map.Entry<String, String> expectedBubble : expectedBubblesMap.entrySet()) {
                    if (!actualBubblesSet.contains(expectedBubble)) {
                        final String message = String.format("Expected bubble with color %s and text '%s' not found",
                                expectedBubble.getKey(), expectedBubble.getValue());
                        return rejected(message, actualBubblesSet.toString());
                    }
                }

                return CheckResult.accepted();
            }

            @Override
            public String toString() {
                return "Expected Bubbles: " + expectedBubblesMap.entrySet().stream()
                        .map(entry -> "Color: " + entry.getKey() + ", Text: " + entry.getValue())
                        .collect(Collectors.joining(", "));
            }
        };
    }
}
