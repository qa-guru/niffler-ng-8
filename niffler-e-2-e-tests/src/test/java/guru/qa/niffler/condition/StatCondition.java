package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebElement;

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
        String expRgba = Arrays.stream(expColors).map(Color::getRgb).toList().toString();
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
                List<String> actualRgba = new ArrayList<>();
                for (int i = 0; i < actElements.size(); i++) {
                    final String actRgbColor = actElements.get(i).getCssValue("background-color");
                    final Color expColor = expColors[i];
                    actualRgba.add(actRgbColor);
                    if (isPassed) {
                        isPassed = expColor.getRgb().equals(actRgbColor);
                    }
                }
                if (!isPassed) {
                    String actRgba = actualRgba.toString();
                    String message = "List colors mismatch (expected: \"%s\", actual: \"%s\")".formatted(expRgba, actRgba);
                    return rejected(message, actRgba);
                }
                return accepted();
            }
        };
    }
}
