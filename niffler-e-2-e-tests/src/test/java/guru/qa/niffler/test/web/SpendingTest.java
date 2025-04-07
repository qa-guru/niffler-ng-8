package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseWebTest {

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(WebUser user, SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .editSpending(spend.description())
                .editDescription(newDescription)
                .checkThatTableContains(newDescription);
    }

}
