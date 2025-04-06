package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseWebTest {

    @Spend(
            username = "user",
            category = "Обучение",
            description = "Обучение Niffler 2.0",
            amount = 89000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";
        openLoginPage()
                .doLoginSuccess("user", "user")
                .editSpending(spend.description())
                .editDescription(newDescription)
                .checkThatTableContains(newDescription);
    }

}
