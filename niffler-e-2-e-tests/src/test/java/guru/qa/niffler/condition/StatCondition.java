package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebElement;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class StatCondition {

    public static WebElementCondition color(Color expColor) {
        return new WebElementCondition("color " + expColor.getRgb()) {

            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(expColor.getRgb().equals(rgba), rgba);
            }
        };
    }

    public static WebElementsCondition color(Color... expColors) {
        String expRgba = Arrays.asList(expColors).toString();
        return new WebElementsCondition() {

            @Override
            public String toString() {
                return expRgba;
            }

            @Override
            public CheckResult check(Driver driver, List<WebElement> actElements) {
                if (ArrayUtils.isEmpty(expColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (actElements.size() != expColors.length) {
                    String message = "List size mismatch (expected: %s, actual: %s)"
                        .formatted(expColors.length, actElements.size());
                    return rejected(message, actElements);
                }
                boolean isPassed = true;
                List<String> actRgbas = new ArrayList<>();
                for (int i = 0; i < actElements.size(); i++) {
                    final String actRgbaColor = actElements.get(i).getCssValue("background-color");
                    final Color expColor = expColors[i];
                    actRgbas.add(actRgbaColor);
                    if (isPassed) {
                        isPassed = expColor.getRgb().equals(actRgbaColor);
                    }
                }
                if (!isPassed) {
                    String actRgba = actRgbas.toString();
                    String message = "List colors mismatch (expected: \"%s\", actual: \"%s\")".formatted(expRgba, actRgba);
                    return rejected(message, actRgba);
                }
                return accepted();
            }
        };
    }

    public static WebElementsCondition statBubbles(List<CategoryBubble> expBubbles) {
        String expBubblesStr = expBubbles.toString();
        return new WebElementsCondition() {

            @Override
            public String toString() {
                return expBubblesStr;
            }

            @Override
            public CheckResult check(Driver driver, List<WebElement> actElements) {
                if (CollectionUtils.isEmpty(expBubbles)) {
                    throw new IllegalArgumentException("No expected category bubbles given");
                }
                if (actElements.size() != expBubbles.size()) {
                    String message = "List size mismatch (expected: %s, actual: %s)"
                        .formatted(expBubbles.size(), actElements.size());
                    return rejected(message, actElements);
                }
                boolean isPassed = true;
                List<CategoryBubble> actBubbles = new ArrayList<>();
                for (int i = 0; i < actElements.size(); i++) {

                    final WebElement actWebElement = actElements.get(i);
                    final String actRgbaColor = actWebElement.getCssValue("background-color");
                    final String actText = actWebElement.getText();

                    final CategoryBubble expBubble = expBubbles.get(i);
                    final String expRgbaColor = expBubble.color().getRgb();
                    final String expText = expBubble.text();

                    actBubbles.add(new CategoryBubble(Color.of(actRgbaColor), actText));
                    if (isPassed) {
                        isPassed = expRgbaColor.equals(actRgbaColor)
                            && expText.equals(actText);
                    }
                }
                if (!isPassed) {
                    String actBubblesStr = actBubbles.toString();
                    String message = "List category bubbles mismatch (expected: \"%s\", actual: \"%s\")"
                        .formatted(expBubblesStr, actBubblesStr);
                    return rejected(message, actBubblesStr);
                }
                return accepted();
            }
        };
    }

    public static WebElementsCondition statBubbles(CategoryBubble... expBubbles) {
        return statBubbles(Arrays.asList(expBubbles));
    }

    public static WebElementsCondition statBubblesInAnyOrder(List<CategoryBubble> expBubbles) {
        String expBubblesStr = expBubbles.toString();
        return new WebElementsCondition() {

            @Override
            public String toString() {
                return expBubblesStr;
            }

            @Override
            public CheckResult check(Driver driver, List<WebElement> actElements) {
                if (CollectionUtils.isEmpty(expBubbles)) {
                    throw new IllegalArgumentException("No expected category bubbles given");
                }
                if (actElements.size() != expBubbles.size()) {
                    String message = "List size mismatch (expected: %s, actual: %s)"
                        .formatted(expBubbles.size(), actElements.size());
                    return rejected(message, actElements);
                }

                List<CategoryBubble> actBubbles = new ArrayList<>();
                for (WebElement actWebElement : actElements) {
                    final String actRgbaColor = actWebElement.getCssValue("background-color");
                    final String actText = actWebElement.getText();
                    actBubbles.add(new CategoryBubble(Color.of(actRgbaColor), actText));
                }

                List<CategoryBubble> expectedCopy = new ArrayList<>(expBubbles);
                List<CategoryBubble> unexpectedBubbles = new ArrayList<>();
                for (CategoryBubble actBubble : actBubbles) {
                    if (!expectedCopy.remove(actBubble)) {
                        unexpectedBubbles.add(actBubble);
                    }
                }
                if (!unexpectedBubbles.isEmpty()) {
                    String message = "Unexpected category bubbles found: %s".formatted(unexpectedBubbles);
                    return rejected(message, actBubbles.toString());
                }

                if (!expectedCopy.isEmpty()) {
                    String message = "Expected category bubbles not found: %s".formatted(expectedCopy);
                    return rejected(message, actBubbles.toString());
                }
                return accepted();
            }
        };
    }

    public static WebElementsCondition statBubblesContains(List<CategoryBubble> expBubbles) {
        String expBubblesStr = expBubbles.toString();
        return new WebElementsCondition() {

            @Override
            public String toString() {
                return "Subset contains: " + expBubblesStr;
            }

            @Override
            public CheckResult check(Driver driver, List<WebElement> actElements) {
                if (CollectionUtils.isEmpty(expBubbles)) {
                    throw new IllegalArgumentException("No expected category bubbles given");
                }

                List<CategoryBubble> actBubbles = new ArrayList<>();
                for (WebElement actWebElement : actElements) {
                    final String actRgbaColor = actWebElement.getCssValue("background-color");
                    final String actText = actWebElement.getText();
                    actBubbles.add(new CategoryBubble(Color.of(actRgbaColor), actText));
                }

                List<CategoryBubble> missingBubbles = new ArrayList<>();
                for (CategoryBubble expBubble : expBubbles) {
                    if (!actBubbles.contains(expBubble)) {
                        missingBubbles.add(expBubble);
                    }
                }

                if (!missingBubbles.isEmpty()) {
                    String message = "Missing expected category bubbles: %s".formatted(missingBubbles);
                    return rejected(message, actBubbles.toString());
                }
                return accepted();
            }
        };
    }
}
