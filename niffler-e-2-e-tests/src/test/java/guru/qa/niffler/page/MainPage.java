package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement statisticsBox = $("#stat");
  private final SelenideElement spendingsHistoryBox = $("#spendings");
  private final SelenideElement openMenuButton = $("button[aria-label='Menu']");
  private final ElementsCollection menuItems = $$("ul[role='menu'] li");

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

  public MainPage verifyMainComponentsVisible() {
    statisticsBox.shouldBe(visible);
    spendingsHistoryBox.shouldBe(visible);
    return this;
  }

  public ProfilePage openProfile() {
    openMenuButton.click();
    menuItems.find(text("Profile"))
      .click();
    return new ProfilePage();
  }

  public FriendsPage openFriends() {
    openMenuButton.click();
    menuItems.find(text("Friends"))
      .click();
    return new FriendsPage();
  }

  public AllPeoplePage openAllPeople() {
    openMenuButton.click();
    menuItems.find(text("All People"))
      .click();
    return new AllPeoplePage();
  }
}
