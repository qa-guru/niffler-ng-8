package guru.qa.niffler.web.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.util.FormatUtil;
import guru.qa.niffler.web.component.DeleteSpendingsAlert;
import guru.qa.niffler.web.component.Header;
import lombok.Getter;
import org.openqa.selenium.Keys;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.StatCondition.color;

public class MainPage extends BasePage {

  @Getter
  private final Header header = new Header();
  @Getter
  private final DeleteSpendingsAlert deleteSpendingsAlert = new DeleteSpendingsAlert();

  private final ElementsCollection spendingTableRows = $$("#spendings tbody tr");
  private final SelenideElement spendingText = $("#spendings h2");
  private final SelenideElement spendingSearchToolbar = $("#spendings .MuiToolbar-root");
  private final SelenideElement spendingTable = $("#spendings table");
  private final SelenideElement statisticText = $("#stat h2");
  private final SelenideElement statisticImg = $("#stat canvas");
  private final SelenideElement searchInput = $("input[placeholder='Search");
  private final SelenideElement statisticImage = $("canvas[role='img']");
  private final ElementsCollection categoryLabels = $$("#legend-container li");
  private final SelenideElement deleteBtn = $("#delete");

  public MainPage findSpending(String spendingDescription) {
    searchInput.setValue(spendingDescription);
    searchInput.sendKeys(Keys.ENTER);
    return this;
  }

  public MainPage clickDeleteBtn() {
    deleteBtn.click();
    return this;
  }

  public MainPage deleteSpending(String spendingDescription) {
    clickSpendingCheckbox(spendingDescription);
    clickDeleteBtn();
    deleteSpendingsAlert.clickDeleteBtn();
    return this;
  }

  public EditSpendingPage editSpending(String spendingDescription) {
    spendingTableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage clickSpendingCheckbox(String spendingDescription) {
    spendingTableRows.find(text(spendingDescription))
        .$$("td")
        .get(0)
        .click();
    return this;
  }

  public MainPage checkThatTableContains(String spendingDescription) {
    spendingTableRows.shouldHave(texts(spendingDescription));
    return this;
  }

  public MainPage checkCategoryBubblesContainsAll(List<SpendJson> spends, Object target, Object replacement) {
    List<String> expCategoryLabels = expCategoryLabels(spends, target, replacement);
    categoryLabels.shouldBe(CollectionCondition.textsInAnyOrder(expCategoryLabels));
    return this;
  }

  public MainPage checkCategoryBubblesContainsAll(List<SpendJson> spends) {
    List<String> expCategoryLabels = expCategoryLabels(spends, null, null);
    categoryLabels.shouldBe(CollectionCondition.textsInAnyOrder(expCategoryLabels));
    return this;
  }

  public MainPage checkCategoryBubbles(Color... color) {
    categoryLabels.should(color(color));
    return this;
  }

  public MainPage checkStatisticScreenshot(BufferedImage expImage) {
    sleepSec(2);
    BufferedImage screenshot = statisticImage.screenshotAsImage();
    checkScreenshot(expImage, screenshot);
    return this;
  }

  public MainPage checkMainPage() {
    statisticText.shouldBe(visible);
    statisticImg.shouldBe(visible);
    spendingText.shouldBe(visible);
    spendingSearchToolbar.shouldBe(visible);
    spendingTable.shouldBe(visible);
    return this;
  }

  private List<String> expCategoryLabels(Collection<SpendJson> spends, Object target, Object replacement) {
    String targetStr;
    String replacementStr;
    boolean isNotNullTarget = target != null;
    if (target instanceof Double dTarget && replacement instanceof Double dReplacement) {
      targetStr = FormatUtil.format(dTarget);
      replacementStr = FormatUtil.format(dReplacement);
    } else {
      targetStr = String.valueOf(target);
      replacementStr = String.valueOf(replacement);
    }
    return spends.stream()
        .map(s -> "%s %s %s".formatted(
            s.category().name(),
            FormatUtil.format(s.amount()),
            s.currency().getSymbol())
        )
        .map(s -> {
          if (isNotNullTarget) {
            if (s.contains(targetStr)) {
              return s.replace(targetStr, replacementStr);
            }
          }
          return s;
        })
        .toList();
  }
}
