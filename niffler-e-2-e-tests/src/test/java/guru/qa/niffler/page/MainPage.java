package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.CommonSteps;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {

    private final SelenideElement table = $("#spendings tbody");
    private final SelenideElement stat = $("canvas[role='img']");
    private final ElementsCollection tableRows = $$("#spendings tbody tr");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    public void checkTableVisible() {
        table.shouldBe(visible);
    }

    public void checkTableNotVisible() {
        table.shouldNotBe(visible);
    }

    public BufferedImage screenshotStats() throws IOException {
        sleep(5000);
       return CommonSteps.screenshot(stat);
    }
}
