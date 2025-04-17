package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement statistics = $("#stat");
  private final SelenideElement historyOfSpendings = $("#spendings");
  private final SelenideElement personIcon = $("svg[data-testid='PersonIcon']");
  private final SelenideElement profileBtn  = $("a.nav-link[href='/profile']");
  private final SelenideElement friendsBtn  = $("a.nav-link[href='/people/friends']");


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

  public void checkLoadingMainPage() {
    statistics.should(visible);
    historyOfSpendings.should(visible);
  }

  public ProfilePage openProfilePage() {
    personIcon.click();
    profileBtn.click();
    return new ProfilePage();
  }

  public FriendsPage openFriendsPage() {
    personIcon.click();
    friendsBtn.click();
    return new FriendsPage();
  }
}
