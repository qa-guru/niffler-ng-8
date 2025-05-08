package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        .editDescription(newDescription);

    new MainPage().checkThatTableContains(newDescription);
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Niffler 2.0",
                  amount = 89000.00
          )
  )
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentTest(UserJson userJson, BufferedImage expected) {
    assertFalse(new ScreenDiffResult(
            expected,
            Selenide.open(CFG.frontUrl(), LoginPage.class)
                    .doLogin(userJson)
                    .getStatImage()
    ));
  }
}
