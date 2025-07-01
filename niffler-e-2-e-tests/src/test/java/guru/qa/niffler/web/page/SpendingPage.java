package guru.qa.niffler.web.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.web.component.Calendar;
import io.qameta.allure.Step;

import java.util.Date;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.$;

public class SpendingPage extends BasePage<SpendingPage> {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement currencyField = $("#currency");
    private final SelenideElement currencyMenu = $("#menu-currency");
    private final SelenideElement categoryInput = $("#category");
    private final Calendar<SpendingPage> calendar = new Calendar<>(this);
    private final SelenideElement saveChangesBtn = $("#save");

    @Step("Создаем трату")
    public MainPage createSpending(SpendJson spend) {
        setAmount(spend.amount());
        setCurrency(spend.currency());
        setCategory(spend.category().name());
        setDate(spend.spendDate());
        setDescription(spend.description());
        clickSaveChanges();
        return new MainPage();
    }

    @Step("Изменяем описание траты")
    public MainPage editDescription(String description) {
        setDescription(description);
        clickSaveChanges();
        return new MainPage();
    }

    @Step("Изменяем стоимость траты")
    public MainPage editAmount(Double amount) {
        setAmount(amount);
        clickSaveChanges();
        return new MainPage();
    }

    @Step("Вписываем стоимость траты")
    public SpendingPage setAmount(Double amount) {
        descriptionInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Выбираем валюту")
    public SpendingPage setCurrency(CurrencyValues currency) {
        currencyField.click();
        currencyMenu.should(appear);
        currencyMenu
            .$("li[data-value='" + currency.getDataValue() + "']")
            .click();
        currencyMenu.should(disappear);
        return this;
    }

    @Step("Вводим категорию")
    public SpendingPage setCategory(String categoryName) {
        categoryInput.clear();
        categoryInput.setValue(categoryName);
        return this;
    }

    @Step("Вводим дату")
    public SpendingPage setDate(Date date) {
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Step("Вписываем описание траты")
    public SpendingPage setDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Кликаем на 'Save changes'")
    public SpendingPage clickSaveChanges() {
        saveChangesBtn.click();
        return this;
    }
}
