package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

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
            .doLoginSuccess(user)
            .findSpending(spendingDescription)
            .editSpending(spendingDescription)
            .editDescription(newDescription)
            .checkThatTableContains(newDescription);
    }

    @User(
        spendings = {
            @Spending(
                category = "Обучение",
                description = "Обучение Niffler 2.0",
                amount = 89000,
                currency = CurrencyValues.RUB
            ),
            @Spending(
                category = "Авто",
                description = "Vossen",
                amount = 400000,
                currency = CurrencyValues.RUB
            )
        }
    )
    @ScreenShotTest(value = "img/exp/spend/check-stat.png")
    void checkStatComponentTest(UserParts user, List<SpendJson> spends, BufferedImage expImage) {
        openLoginPage()
            .doLoginSuccess(user)
            .checkStatisticScreenshot(expImage)
            .checkCategoryLabelsContainsAll(spends);
    }

    @User(
        spendings = {
            @Spending(
                category = "Обучение",
                description = "Обучение Niffler 2.0",
                amount = 89000,
                currency = CurrencyValues.RUB
            ),
            @Spending(
                category = "Авто",
                description = "Vossen",
                amount = 400000,
                currency = CurrencyValues.RUB
            )
        }
    )
    @ScreenShotTest(value = "img/exp/spend/remove-element.png")
    void checkStatComponentRemoveElement(UserParts user, List<SpendJson> spends, BufferedImage expImage) {
        String description = spends.getFirst().description();
        List<SpendJson> expSpends = spends.stream().filter(s -> !description.equals(s.description())).toList();

        openLoginPage()
            .doLoginSuccess(user)
            .checkCategoryLabelsContainsAll(spends)
            .deleteSpending(description)
            .checkCategoryLabelsContainsAll(expSpends)
            .checkStatisticScreenshot(expImage);
    }

    @User(
        spendings = {
            @Spending(
                category = "Обучение",
                description = "Обучение Niffler 2.0",
                amount = 89000,
                currency = CurrencyValues.RUB
            ),
            @Spending(
                category = "Авто",
                description = "Vossen",
                amount = 400000,
                currency = CurrencyValues.RUB
            )
        }
    )
    @ScreenShotTest(value = "img/exp/spend/update-element.png")
    void checkStatComponentUpdateElement(UserParts user, List<SpendJson> spends, BufferedImage expImage) {
        SpendJson spend = spends.getFirst();
        String description = spend.description();
        double newAmount = 1450.57;
        double oldAmount = spend.amount();

        openLoginPage()
            .doLoginSuccess(user)
            .checkCategoryLabelsContainsAll(spends)
            .editSpending(description)
            .editAmount(newAmount)
            .checkStatisticScreenshot(expImage)
            .checkCategoryLabelsContainsAll(spends, oldAmount, newAmount);
    }

    @User(
        categories = @Category(name = "Авто", archived = true),
        spendings = {
            @Spending(
                category = "Обучение",
                description = "Обучение Niffler 2.0",
                amount = 89000,
                currency = CurrencyValues.RUB
            ),
            @Spending(
                category = "Авто",
                description = "Vossen",
                amount = 400000,
                currency = CurrencyValues.RUB
            )
        }
    )
    @ScreenShotTest(value = "img/exp/spend/archived-element.png")
    void checkStatComponentArchivedElement(UserParts user, List<SpendJson> spends, BufferedImage expImage) {
        openLoginPage()
            .doLoginSuccess(user)
            .checkStatisticScreenshot(expImage)
            .checkCategoryLabelsContainsAll(spends, "Авто", "Archived");
    }
}
