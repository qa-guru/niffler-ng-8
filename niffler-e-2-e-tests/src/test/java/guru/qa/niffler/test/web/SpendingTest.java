package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

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
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
                .editSpending(spend.description())
                .editDescription(newDescription)
                .checkThatTableContains(newDescription);
    }

}
