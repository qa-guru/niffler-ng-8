package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement iconButton = $("[class*='MuiAppBar-root'] button");
  private final ElementsCollection modalWindowButtons = $$("[class='link nav-link']");
  private final SelenideElement archivedCheckbox = $("[class*='PrivateSwitchBase-input']");
  private final ElementsCollection archivedCategories = $$("[class*='MuiChip-labelMedium']");

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

  public MainPage iconSubmit() {
    iconButton.click();

    return this;
  }

  public MainPage profileSubmit() {
    modalWindowButtons.get(0).click();

    return this;
  }

  public MainPage archivedCheckboxSubmit() {
    archivedCheckbox.click();

    return this;
  }

  public MainPage shouldArchivedCategory(String categoryName) {
    final SelenideElement category = archivedCategories.stream().filter(element -> element.text().equals(categoryName)).findFirst().get();
    category.shouldBe(visible);

    return this;
  }

}
