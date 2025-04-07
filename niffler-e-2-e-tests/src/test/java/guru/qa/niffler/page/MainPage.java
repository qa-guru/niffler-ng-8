package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement iconButton = $("[class*='MuiAppBar-root'] button");
  private final ElementsCollection modalWindowItem = $$("[class='link nav-link']");
  private final SelenideElement statistics = $("#stat [class*='MuiTypography']");
  private final SelenideElement historyOfSpendings = $("#spendings [class*='MuiTypography-h']");

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

  public ProfilePage profileSubmit() {
    modalWindowItem.get(0).click();
    return new ProfilePage();
  }

  public FriendsPage friendsSubmit() {
    modalWindowItem.get(1).click();
    return new FriendsPage();
  }

  public MainPage shouldStatisticsVisible() {
    statistics.shouldBe(visible);
    return this;
  }

  public void shouldHistoryOfSpendingVisible() {
    historyOfSpendings.shouldBe(visible);
  }
}
