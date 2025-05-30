package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static guru.qa.niffler.utils.RandomDataUtils.randomSpendingAmount;
import static guru.qa.niffler.utils.RandomDataUtils.randomSpendingDescription;

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
        .getSpendTable()
        .editSpending(spend.description())
        .editDescription(newDescription)
        .getSpendTable()
        .checkSpends(userJson);
  }

  @User(
          spendings = @Spending(
                  amount = 89000.00
          )
  )
  @ScreenShotTest(value = "img/expected-stat.png")
  void checkStatComponentTest(UserJson userJson, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(userJson)
            .getStatComponent()
            .checkStatisticDiagramInfoUnEditable(userJson)
            .checkStatisticImage(expected)
            .checkBubbles(
                    Bubble.bubble(
                            Color.yellow,
                            userJson
                                    .spends()
                                    .getFirst()
                    )
            )
            .getSpendsTable()
            .checkSpendsInAnyOrder(userJson);

  }

  @User(
          spendings = {
                  @Spending(
                          amount = 45391.0
                  )
          })
  @ScreenShotTest(value = "img/expected-stat-edit.png")
  void checkStatComponentAfterEditingTest(UserJson user, BufferedImage expected) throws IOException {
    SpendJson spend = user.testData().spendings().getFirst();
    double newSum = spend.amount()+50;
    SpendJson spendEdited = new SpendJson(
            spend.id(),
            spend.spendDate(),
            spend.category(),
            spend.currency(),
            newSum,
            spend.description(),
            spend.username()
    );

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .getSpendTable()
            .editSpending(spend.description())
            .editSum(String.valueOf(newSum))
            .getStatComponent()
            .checkStatisticDiagramInfo(Map.of(spend.category(),newSum))
            .checkStatisticImage(expected)
            .getSpendsTable()
            .checkSpend(spendEdited);

  }

  @User(
          spendings = @Spending())
  @ScreenShotTest(value = "img/expected-stat-delete.png")
  void checkStatComponentAfterDeletingSpendTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .getSpendTable()
            .deleteSpending(user.spends().getFirst().description())
            .getStatComponent()
            .checkStatisticImage(expected);
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
  void checkStatComponentWithArchiveSpendTest(UserJson user, BufferedImage expected) throws IOException {

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .getStatComponent()
            .checkStatisticImage(expected)
            .checkStatisticDiagramInfoUnEditable(user)
            .getSpendsTable()
            .checkContainsSpends(user);
  }

  @Test
  @User(categories = {@Category})
  void addSpendTest(UserJson user){
      SpendJson spend = new SpendJson(
              user.id(),
              new Date(System.currentTimeMillis()),
              user.testData().categories().getFirst(),
              CurrencyValues.RUB,
              randomSpendingAmount(),
              randomSpendingDescription(),
              user.username()
      );
      Selenide.open(CFG.frontUrl(), LoginPage.class)
              .doLogin(user.username(), user.testData().password())
              .getHeader()
              .addSpendingPage()
              .fillPage(spend);

      new MainPage().getSpendTable()
              .checkSpend(spend);
  }

    @Test
    @User
    void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .addSpendingPage()
                .setNewSpendingCategory("Friends")
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Amount has to be not less then 0.01");
    }

}
