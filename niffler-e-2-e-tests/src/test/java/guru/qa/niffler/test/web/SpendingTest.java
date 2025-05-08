package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;


import java.awt.image.BufferedImage;
import java.util.Map;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      spendings = @Spending(
        category = "Обучение",
        description = "Обучение Niffler 2.0",
        amount = 89000.00
      )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson userJson) {
    final SpendJson spend = userJson.testData().spendings().getFirst();
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(userJson)
        .editSpending(spend.description())
        .editDescription(newDescription)
        .checkThatTableContains(newDescription);
  }

  @User(
          spendings = @Spending(
                  amount = 89000.00
          )
  )
  @ScreenShotTest(value = "img/expected-stat.png")
  void checkStatComponentTest(UserJson userJson, BufferedImage expected) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(userJson)
            .checkStatisticDiagram(expected)
            .checkStatisticDiagramInfoUnEditable(userJson);
  }

  @User(
          spendings = {
                  @Spending(
                          amount = 45391.0
                  )
          })
  @ScreenShotTest(value = "img/expected-stat-edit.png")
  void checkStatComponentAfterEditingTest(UserJson user, BufferedImage expected) {
    SpendJson spend = user.testData().spendings().getFirst();
    double newSum = spend.amount()+50;

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .editSpending(spend.description())
            .editSum(String.valueOf(newSum))
            .checkStatisticDiagramInfo(Map.of(spend.category(),newSum))
            .checkStatisticDiagram(expected);

  }

  @User(
          spendings = @Spending())
  @ScreenShotTest(value = "img/expected-stat-delete.png")
  void checkStatComponentAfterDeletingSpendTest(UserJson user, BufferedImage expected) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .deleteSpending(user.testData().spendings().getFirst().description())
            .checkStatisticDiagram(expected);
  }

  @User(
          categories = {
                  @Category(
                          name = "Дом",
                          archived = true
                  )
          },
          spendings = {
                  @Spending(
                          category = "Путешествия",
                          amount = 40000
                  ),
                  @Spending(
                          amount = 42000
                  ),
                  @Spending(
                          category = "Дом",
                          amount = 55000
                  )
          })
  @ScreenShotTest(value = "img/expected-stat-archived.png")
  void checkStatComponentWithArchiveSpendTest(UserJson user, BufferedImage expected) {

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkStatisticDiagram(expected)
            .checkStatisticDiagramInfoUnEditable(user);
  }
}
