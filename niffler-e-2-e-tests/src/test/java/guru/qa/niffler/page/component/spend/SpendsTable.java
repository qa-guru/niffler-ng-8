package guru.qa.niffler.page.component.spend;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.BaseComponent;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static guru.qa.niffler.condition.SpendConditions.*;

public class SpendsTable extends BaseComponent<SpendsTable> {

    private final ElementsCollection tableRows = self.$$("tr");
    private final SelenideElement searchInput;
    private final SelenideElement deleteBtn;
    private final SelenideElement dialogWindow;

    public SpendsTable(SelenideDriver driver) {
        super(driver, driver.$("#spendings tbody"));
        this.searchInput = $("input");
        this.deleteBtn = $("#delete");
        this.dialogWindow = $("div[role='dialog']");
    }
    public SpendsTable() {
        super(Selenide.$("#spendings tbody"));
        this.searchInput = $("input");
        this.deleteBtn = $("#delete");
        this.dialogWindow = $("div[role='dialog']");
    }

    @Step("Check that table contains description {0}")
    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    @Step("Удалить spending с description {0}")
    public MainPage deleteSpending(String spendingDescription) {
        SelenideElement rowElement = tableRows.find(text(spendingDescription));
        SpendRow row = new SpendRow(driver,rowElement);
        row.clickCheckBox();
        deleteBtn.click();
        dialogWindow.$(byText("Delete")).click();
        return new MainPage(driver);
    }

    @Step("Изменить spending с description {0}")
    public EditSpendingPage editSpending(String spendingDescription) {
        searchInput.setValue(spendingDescription).pressEnter();
        SelenideElement rowElement = tableRows.find(text(spendingDescription));
        SpendRow row = new SpendRow(driver,rowElement);
        return row.editSpend();
    }

    @Step("Check that spends equals expected spends {spend}")
    @Nonnull
    public SpendsTable checkSpend(SpendJson spend) {
        tableRows.find(text(spend.description()))
                .should(spend(spend));
        return this;
    }


    @Step("Check that spends equals expected spends {user.testData.spendings}")
    @Nonnull
    public SpendsTable checkSpends(UserJson user) {
        tableRows.should(spendRows(user));
        return this;
    }

    @Step("Check that spends equals expected spends in any order {user.testData.spendings}")
    @Nonnull
    public SpendsTable checkSpendsInAnyOrder(UserJson user) {
        tableRows.should(spendRowsInAnyOrder(user));
        return this;
    }

    @Step("Check that spends contains expected spends {user.testData.spendings}")
    @Nonnull
    public SpendsTable checkContainsSpends(UserJson user) {
        tableRows.should(spendRowsContains(user));
        return this;
    }
}
