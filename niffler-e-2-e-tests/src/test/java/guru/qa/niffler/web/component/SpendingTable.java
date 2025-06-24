package guru.qa.niffler.web.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.web.model.DataFilterValues;
import guru.qa.niffler.web.page.SpendingPage;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendConditions.spends;
import static guru.qa.niffler.util.ConstructObjectUtils.expSpendings;

public class SpendingTable<P> extends BaseComponent<P> {

    private final Alert<SpendingTable<?>> deleteSpendingsAlert = new Alert<>(this);

    private final SelenideElement periodDropDownValues = $("ul[role='listbox']");
    private final SelenideElement periodDropDownList = self.$("#period");
    private final ElementsCollection spendingTableRows = self.$$("#spendings tbody tr");
    private final SelenideElement deleteBtn = self.$("#delete");
    private final SelenideElement searchInput = self.$("input[placeholder='Search");

    public SpendingTable(P currentPage) {
        super(currentPage, $("div.MuiTableContainer-root"));
    }

    @Step("Ищем траты по описанию")
    public SpendingTable<P> findSpending(String spendingDescription) {
        searchInput.setValue(spendingDescription);
        searchInput.sendKeys(Keys.ENTER);
        return this;
    }

    public SpendingTable<P> selectPeriod(DataFilterValues period) {
        periodDropDownList.click();
        periodDropDownValues.shouldBe(visible)
            .$("[data-value='" + period.getDataValue() + "']")
            .click();
        return this;
    }

    public SpendingPage editSpending(String description) {
        findSpending(description);
        spendingTableRows.find(text(description))
            .$$("td")
            .get(5)
            .click();
        return new SpendingPage();
    }

    @Step("Удаляем трату по описанию")
    public SpendingTable<P> deleteSpending(String description) {
        clickSpendingCheckbox(description);
        clickDeleteBtn();
        deleteSpendingsAlert.clickAcceptBtn();
        return this;
    }

    @Step("Кликаем по кнопке удалить")
    public SpendingTable<P> clickDeleteBtn() {
        deleteBtn.click();
        return this;
    }

    @Step("Выбираем трату по описанию")
    public SpendingTable<P> clickSpendingCheckbox(String description) {
        spendingTableRows.find(text(description))
            .$$("td")
            .get(0)
            .click();
        return this;
    }

    @Step("Проверяем наличие описания в тратах")
    public SpendingTable<P> checkTabContains(String... expSpends) {
        spendingTableRows.shouldHave(texts(expSpends));
        return this;
    }

    @Step("Проверяем количество строк в таблице")
    public SpendingTable<P> checkTableSize(int expSize) {
        spendingTableRows.shouldHave(size(expSize));
        return this;
    }

    @Step("Проверяем таблицу трат")
    public SpendingTable<P> checkSpendings(List<SpendJson> spends, Object target, Object replacement) {
        List<SpendJson> spendJsons = expSpendings(spends, target, replacement);
        spendingTableRows.shouldBe(spends(spendJsons));
        return this;
    }

    @Step("Проверяем таблицу трат")
    public SpendingTable<P> checkSpendings(List<SpendJson> spends) {
        List<SpendJson> spendJsons = expSpendings(spends, null, null);
        spendingTableRows.shouldBe(spends(spendJsons));
        return this;
    }
}
