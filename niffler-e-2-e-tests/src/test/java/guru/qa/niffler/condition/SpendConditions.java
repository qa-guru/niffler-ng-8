package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.api.model.SpendJson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendConditions {

    public static @Nonnull WebElementsCondition spends(List<SpendJson> expSpends) {
        String expSpendsStr = expSpends.stream().map(SpendConditions::map).toList().toString();
        return new WebElementsCondition() {

            @Override
            public String toString() {
                return expSpendsStr;
            }

            @Override
            public CheckResult check(Driver driver, List<WebElement> actElements) {
                if (CollectionUtils.isEmpty(expSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                if (actElements.size() != expSpends.size()) {
                    String message = "List size mismatch (expected: %s, actual: %s)"
                        .formatted(expSpends.size(), actElements.size());
                    return rejected(message, actElements);
                }

                boolean isPassed = true;
                List<SpendPart> actSpends = new ArrayList<>();
                for (int i = 0; i < actElements.size(); i++) {
                    SpendPart actSpend = map(actElements.get(i).findElements(By.cssSelector("td")));
                    SpendPart expSpend = map(expSpends.get(i));
                    actSpends.add(actSpend);
                    if (isPassed) {
                        isPassed = expSpend.equals(actSpend);
                    }
                }
                if (!isPassed) {
                    String actSpendsStr = actSpends.toString();
                    String message = "List colors mismatch (expected: \"%s\", actual: \"%s\")".formatted(expSpendsStr, actSpendsStr);
                    return rejected(message, actSpendsStr);
                }
                return accepted();
            }
        };
    }

    public static @Nonnull WebElementsCondition spends(SpendJson... expectedSpends) {
        return spends(Arrays.asList(expectedSpends));
    }

    private static @Nonnull SpendPart map(List<WebElement> columns) {
        String[] currency = columns.get(2).getText().split(" ");
        return new SpendPart(
            columns.get(1).getText(),
            Double.valueOf(currency[0]),
            currency[1],
            columns.get(3).getText(),
            columns.get(4).getText()
        );
    }

    private static @Nonnull SpendPart map(SpendJson spendJson) {
        return new SpendPart(
            spendJson.category().name(),
            spendJson.amount(),
            spendJson.currency().getSymbol(),
            spendJson.description(),
            formatDate(spendJson.spendDate())
        );
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(date);
    }

    public record SpendPart(String categoryName, Double amount, String currency, String description, String date) {
    }

}
