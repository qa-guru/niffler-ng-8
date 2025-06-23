package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.SelectField;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;


@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  private final SelectField currencySelect = new SelectField($("#currency"));

  private final SelenideElement sumInput = $("#amount");
  private final SelenideElement categoryInput = $("#category");
  private final ElementsCollection categories = $$(".MuiChip-root");
  private final SelenideElement descriptionInput = $("#description");

  private final SelenideElement cancelBtn = $("#cancel");
  private final SelenideElement submitBtn = $("#save");

  @Getter
  private final Calendar calendar = new Calendar(driver);

  public EditSpendingPage(@Nullable SelenideDriver driver){
    super(driver);
  }

  public EditSpendingPage(){
    super();
  }

  @Override
  public String getUrl() {
    return "spending";
  }

  @Step("Edit description to {description}")
  public MainPage editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
    return new MainPage(driver);
  }

  @Step("Edit sum to {sum}")
  public MainPage editSum(String sum) {
    sumInput.clear();
    sumInput.setValue(sum);
    submitBtn.click();
    return new MainPage(driver);
  }

  @Step("Fill spending data from object")
  @Nonnull
  public EditSpendingPage fillPage(SpendJson spend) {
    return setNewSpendingDate(spend.spendDate())
            .setNewSpendingAmount(spend.amount())
            .setNewSpendingCurrency(spend.currency())
            .setSpendingCategory(spend.category().name())
            .setNewSpendingDescription(spend.description());
  }

  @Step("Select new spending currency: '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingCurrency(CurrencyValues currency) {
    currencySelect.setValue(currency.name());
    return this;
  }

  @Step("Select old spending currency: '{0}'")
  @Nonnull
  public EditSpendingPage setOldSpendingCategory(String category) {
    categories.find(text(category))
            .shouldBe(visible)
            .click();
    return this;
  }

  @Step("Select spending category: '{0}'")
  @Nonnull
  public EditSpendingPage setSpendingCategory(String category) {
    if(categories.find(text(category)).exists()){
      setOldSpendingCategory(category);
    } else {
      setNewSpendingCategory(category);
    }
    return this;
  }

  @Step("Select new spending category: '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingCategory(String category) {
    categoryInput.clear();
    categoryInput.setValue(category);
    return this;
  }

  @Step("Set new spending amount: '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingAmount(double amount) {
    sumInput.clear();
    sumInput.setValue(String.valueOf(amount));
    return this;
  }

  @Step("Set new spending amount: '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingAmount(int amount) {
    sumInput.clear();
    sumInput.setValue(String.valueOf(amount));
    return this;
  }

  @Step("Set new spending date: '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingDate(Date date) {
    calendar.selectDateInCalendar(date);
    return this;
  }

  @Step("Set new spending description: '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Click submit button to create new spending")
  @Nonnull
  public EditSpendingPage saveSpending() {
    submitBtn.click();
    return this;
  }
}
