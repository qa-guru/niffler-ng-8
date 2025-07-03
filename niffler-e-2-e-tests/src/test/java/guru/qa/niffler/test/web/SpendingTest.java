package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.enums.CurrencyValues;
import guru.qa.niffler.jupiter.annotations.Spend;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class SpendingTest extends BaseUITest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "test",
            spendings = @Spend(username = "duck",
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB))
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("test", "12345");
        mainPage().table.editSpendingByDescription(spend.description());
        spendingPage().description.clearThenFill(newDescription);

        mainPage().table.checkTableContainsSpendingByDescription(newDescription);
    }

    @Test
    void checkHtmlTest(){
        SpendApiClient client = new SpendApiClient();
        client.addSpend(RandomDataUtils.generateSpend("test", 100.44));
    }
}
