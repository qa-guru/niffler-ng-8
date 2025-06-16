package guru.qa.niffler.page.component;

import guru.qa.niffler.page.component.basicComponents.Table;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SpendingTable extends Table {

    public SpendingTable editSpendingByDescription(String description) {
        findRowByText(description).$("//button").click();
        return this;
    }

    public SpendingTable deleteSpendingByDescription(String description) {
        findRowByText(description).$("//input").click();
        $("//button[.='Delete']").click();
        return this;
    }

    public SpendingTable checkTableContainsSpendingByDescription(String description) {
        findRowByText(description).shouldBe(visible);
        return this;
    }
}