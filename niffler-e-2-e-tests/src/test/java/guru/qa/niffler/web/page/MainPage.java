package guru.qa.niffler.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.web.component.Header;
import lombok.Getter;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage {

  @Getter
  private final Header header = new Header();
  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement spendingText = $("#spendings h2");
  private final SelenideElement spendingSearchToolbar = $("#spendings .MuiToolbar-root");
  private final SelenideElement spendingTable = $("#spendings table");
  private final SelenideElement statisticText = $("#stat h2");
  private final SelenideElement statisticImg = $("#stat canvas");
  private final SelenideElement searchInput = $("input[placeholder='Search");

  public MainPage findSpending(String spendingDescription) {
    searchInput.setValue(spendingDescription);
    searchInput.sendKeys(Keys.ENTER);
    return this;
  }

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

  public MainPage checkMainPage() {
    statisticText.shouldBe(visible);
    statisticImg.shouldBe(visible);
    spendingText.shouldBe(visible);
    spendingSearchToolbar.shouldBe(visible);
    spendingTable.shouldBe(visible);
    return this;
  }

}
