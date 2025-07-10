package guru.qa.niffler.data.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.InputGenerator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.List;

public class SpendCondition {

    private static By cells = By.xpath(".//td");

    private static List<WebElement> getRowCells(WebElement webElement) {
        return webElement.findElements(cells);
    }

    public static WebElementCondition rowShouldExist(@Nonnull SpendJson spend) {

        return new WebElementCondition("Expected: category = " + spend.category().name()
                + ", amount = " + spend.amount() + ", description = " + spend.description() + ", date" + spend.spendDate()) {

            @Override
            public CheckResult check(Driver driver, @Nonnull WebElement webElement) {

                List<WebElement> cells = getRowCells(webElement);

                if (!cells.get(1).getText().equals(spend.category().name())) {
                    String message = String.format(
                            "Spend category mismatch (expected: %s, actual: %s)",
                            spend.category().name(), cells.get(1).getText()
                    );
                    return CheckResult.rejected(message, cells.get(1).getText());
                }
                if (!cells.get(2).getText().equals(InputGenerator.getExpectedSpendAmount(spend.amount()))) {
                    String message = String.format(
                            "Spend amount mismatch (expected: %s, actual: %s)",
                            spend.amount(), cells.get(2).getText()
                    );
                    return CheckResult.rejected(message, cells.get(2).getText());
                }
                if (!cells.get(3).getText().equals(spend.description())) {
                    String message = String.format(
                            "Spend description mismatch (expected: %s, actual: %s)",
                            spend.description(), cells.get(3).getText()
                    );
                    return CheckResult.rejected(message, cells.get(3).getText());
                }
                if (!cells.get(4).getText().equals(InputGenerator.getExpectedSpendDate(spend.spendDate()))) {
                    String message = String.format(
                            "Spend date mismatch (expected: %s, actual: %s)",
                            spend.spendDate().toString(), cells.get(4).getText()
                    );
                    return CheckResult.rejected(message, cells.get(4).getText());
                }

                return CheckResult.accepted();
            }
        };
    }
}