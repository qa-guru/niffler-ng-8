package guru.qa.niffler.page.component.spend;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.component.BaseComponent;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Arrays;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static guru.qa.niffler.condition.SpendConditions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendsTable extends BaseComponent<SpendsTable> {

    private final ElementsCollection tableRows = self.$$("tr");
    private final SelenideElement deleteBtn = $("#delete");
    private final SelenideElement dialogWindow = $("div[role='dialog']");
    private final SelenideElement dataFilterDropDown = $("div[id='period']");
    private final SelenideElement dataFilterDropDownContainer = $("ul[role='listbox']");

    @Getter
    private SearchField searchField = new SearchField(driver);

    @Getter
    private final StatComponent statComponent = new StatComponent(driver);

    public SpendsTable(@Nullable SelenideDriver driver) {
        super(driver,
                driver == null
                ? Selenide.$("#spendings tbody")
                : driver.$("#spendings tbody")
        );
    }

    public SpendsTable() {
        super(Selenide.$("#spendings tbody"));
    }

    @Step("Select period {0}")
    public SpendsTable selectPeriod(DataFilterValues period){
        dataFilterDropDown.click();
        dataFilterDropDownContainer.shouldBe(visible);
        dataFilterDropDownContainer
                .$$("li")
                .find(
                        attribute(
                                "data-value",
                                period.toString()
                        )
                )
                .click();
        dataFilterDropDown.shouldHave(text(period.getText()));
        return this;
    }

    @Step("Check that table contains description {0}")
    public SpendsTable checkTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
        return this;
    }

    @Step("Check that table size equals {expectedSize}")
    public SpendsTable checkTableSize(int expectedSize) {
        assertEquals(tableRows.size(),expectedSize);
        return this;
    }


    public SpendsTable checkTableContains(String... spendingDescription) {
        Arrays.stream(spendingDescription)
                .forEach(
                        s -> tableRows
                                .find(text(s))
                                .should(visible)
                );
        return this;
    }

    @Step("Удалить spending с description {0}")
    public SpendsTable deleteSpending(String spendingDescription) {
        SelenideElement rowElement = tableRows.find(text(spendingDescription));
        SpendRow row = driver == null
                ? new SpendRow(rowElement)
                : new SpendRow(driver,rowElement);
        row.clickCheckBox();
        deleteBtn.click();
        dialogWindow.$(byText("Delete")).click();
        return this;
    }

    @Step("Изменить spending с description {0}")
    public EditSpendingPage editSpending(String spendingDescription) {
        searchField.search(spendingDescription);
        SelenideElement rowElement = tableRows.find(text(spendingDescription));
        SpendRow row = driver == null
                ? new SpendRow(rowElement)
                : new SpendRow(driver,rowElement);
        return row.editSpend();
    }

    @Step("Check that spends equals expected spends {spend}")
    public SpendsTable checkSpend(SpendJson spend) {
        tableRows.find(text(spend.description()))
                .should(spend(spend));
        return this;
    }


    @Step("Check that spends equals expected spends {user.testData.spendings}")
    public SpendsTable checkSpends(UserJson user) {
        tableRows.should(spendRows(user));
        return this;
    }

    @Step("Check that spends equals expected spends in any order {user.testData.spendings}")
    public SpendsTable checkSpendsInAnyOrder(UserJson user) {
        tableRows.should(spendRowsInAnyOrder(user));
        return this;
    }

    @Step("Check that spends contains expected spends {user.testData.spendings}")
    public SpendsTable checkContainsSpends(UserJson user) {
        tableRows.should(spendRowsContains(user));
        return this;
    }
}
