package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement statisticsText = $x("//h2[contains(text(), \"Statistics\")]");
  private final SelenideElement spendingText = $x("//h2[contains(text(), \"History of Spendings\")]");
  private final SelenideElement profileBtn = $x("//*[@data-testid='PersonIcon']");
  private final SelenideElement profileLink = $("a[href='/profile']");

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

  public void checkThatMainPageOpened() {
    statisticsText.shouldHave(text("Statistics"));
    spendingText.shouldHave(text("History of Spendings"));
  }

  public ProfilePage goToProfile() {
    profileBtn.click();
    profileLink.click();
    return new ProfilePage();
  }
}
