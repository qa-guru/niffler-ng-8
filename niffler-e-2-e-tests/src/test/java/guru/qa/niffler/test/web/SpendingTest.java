package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.util.ScreeDiffResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Selenide.$;

public class SpendingTest extends BaseWebTest {

    @Test
    @User(
        spendings = @Spending(
            category = "Обучение",
            description = "Обучение Niffler 2.0",
            amount = 89000.00,
            currency = CurrencyValues.RUB
        )
    )
    void spendingDescriptionShouldBeUpdatedByTableAction(UserParts user, SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";
        String spendingDescription = spend.description();
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
            .findSpending(spendingDescription)
            .editSpending(spendingDescription)
            .editDescription(newDescription)
            .checkThatTableContains(newDescription);
    }

    @User(
        spendings = @Spending(
            category = "Обучение",
            description = "Обучение Niffler 2.0",
            amount = 89000.00,
            currency = CurrencyValues.RUB
        )
    )
    @ScreenShotTest("img/expected-stat.png")
    @SneakyThrows
    void checkStatComponentTest(UserParts user, SpendJson spend, BufferedImage exp) {
        final String newDescription = "Обучение Niffler NG";
        String spendingDescription = spend.description();
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
            .findSpending(spendingDescription)
            .editSpending(spendingDescription)
            .editDescription(newDescription)
            .checkThatTableContains(newDescription);

        BufferedImage act = ImageIO.read($("canvas[role='ing']").screenshot());
        Assertions.assertFalse(new ScreeDiffResult(act, exp));
    }

}
