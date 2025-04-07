package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      username = "Timofey",
      spendings = @Spending(
        category = "Обучение",
        description = "Обучение Niffler 2.0",
        amount = 89000.00,
        currency = CurrencyValues.RUB
      )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(spend.username(), "123")
        .editSpending(spend.description())
        .editDescription(newDescription);

    new MainPage().checkThatTableContains(newDescription);
  }
}
