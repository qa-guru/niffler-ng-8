package guru.qa.niffler.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.condition.CategoryBubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.web.component.Header;
import guru.qa.niffler.web.component.SpendingTable;
import io.qameta.allure.Step;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.StatCondition.*;
import static guru.qa.niffler.util.ConstructObjectUtils.expCategoryBubbles;

public class MainPage extends BasePage {

  @Getter
  private final Header<MainPage> header = new Header<>(this);
  @Getter
  private final SpendingTable<MainPage> spendingTable = new SpendingTable<>(this);

  private final SelenideElement spendingText = $("#spendings h2");
  private final SelenideElement spendingSearchToolbar = $("#spendings .MuiToolbar-root");
  private final SelenideElement statisticText = $("#stat h2");
  private final SelenideElement statisticImg = $("#stat canvas");
  private final SelenideElement statisticImage = $("canvas[role='img']");
  private final ElementsCollection categoryBubbles = $$("#legend-container li");

  @Step("Проверяем цвета трат")
  public MainPage checkCategoryBubbles(Color... color) {
    categoryBubbles.shouldHave(color(color));
    return this;
  }

  @Step("Проверяем цвета и текст трат")
  public MainPage checkCategoryBubbles(CategoryBubble... color) {
    categoryBubbles.shouldBe(statBubbles(color));
    return this;
  }

  @Step("Проверяем цвета и текст трат")
  public MainPage checkCategoryBubbles(List<SpendJson> spends, Object target, Object replacement) {
    List<CategoryBubble> expCategoryBubbles = expCategoryBubbles(spends, target, replacement);
    categoryBubbles.shouldBe(statBubbles(expCategoryBubbles));
    return this;
  }

  @Step("Проверяем цвета и текст трат")
  public MainPage checkCategoryBubbles(List<SpendJson> spends) {
    List<CategoryBubble> expCategoryBubbles = expCategoryBubbles(spends, null, null);
    categoryBubbles.shouldBe(statBubbles(expCategoryBubbles));
    return this;
  }

  @Step("Проверяем цвета и текст трат")
  public MainPage checkCategoryBubblesInAnyOrder(List<SpendJson> spends, Object target, Object replacement) {
    List<CategoryBubble> expCategoryBubbles = expCategoryBubbles(spends, target, replacement);
    categoryBubbles.shouldBe(statBubblesInAnyOrder(expCategoryBubbles));
    return this;
  }

  @Step("Проверяем картинку статистики")
  public MainPage checkStatisticScreenshot(BufferedImage expImage) {
    sleepSec(2);
    BufferedImage screenshot = statisticImage.screenshotAsImage();
    checkScreenshot(expImage, screenshot);
    return this;
  }

  @Step("Проверяем главную страницу")
  public MainPage checkMainPage() {
    statisticText.shouldBe(visible);
    statisticImg.shouldBe(visible);
    spendingText.shouldBe(visible);
    spendingSearchToolbar.shouldBe(visible);
    spendingTable.getSelf().shouldBe(visible);
    return this;
  }
}
