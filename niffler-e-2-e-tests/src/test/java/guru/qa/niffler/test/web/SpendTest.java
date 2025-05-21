package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenDifResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spend(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .editDescription(newDescription)
                .save();

        new MainPage().checkThatTableContains(newDescription);
    }

    @User(
            spendings = @Spend(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 79990.00,
                    currency = CurrencyValues.RUB
            )
    )

    @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException{
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());
        Selenide.sleep(3000);
        BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());

        assertFalse(new ScreenDifResult(
                expected,
                actual
        ));
    }

    @User(
            spendings = {
                    @Spend(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 10000.00,
                    currency = CurrencyValues.RUB
            ),
                    @Spend(
                            category = "Развлечения",
                            description = "Обучение Niffler 3.0",
                            amount = 20000.00,
                            currency = CurrencyValues.RUB
                    )
            }

    )
    @ScreenShotTest(value = "img/expected_stat_2_spending.png", rewriteExpected = true)
    void checkStatComponent2SpendingTest(UserJson user, BufferedImage expected) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .verifyStatDiagram(expected);
    }

    @User(
            spendings = {
                    @Spend(
                            category = "Обучение",
                            description = "Обучение Niffler 2.0",
                            amount = 10000.00,
                            currency = CurrencyValues.RUB
                    ),
                    @Spend(
                            category = "Развлечения",
                            description = "Обучение Niffler 3.0",
                            amount = 20000.00,
                            currency = CurrencyValues.RUB
                    )
            }

    )
    @ScreenShotTest(value = "img/expected_stat_edit_spending.png", rewriteExpected = true)
    void checkStatComponentEditSpendingTest(UserJson user, BufferedImage expected) {
        final String newAmount = "5000";
        final List<String> statSpendingList = List.of(
                "Развлечения 20000 ₽",
                "Обучение 5000 ₽"
        );
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .editAmount(newAmount)
                .shouldHaveStatSpendings(statSpendingList)
                .verifyStatDiagram(expected);
    }

    @User(
            spendings = {
                    @Spend(
                            category = "Обучение",
                            description = "Обучение Niffler 2.0",
                            amount = 10000.00,
                            currency = CurrencyValues.RUB
                    ),
                    @Spend(
                            category = "Развлечения",
                            description = "Обучение Niffler 3.0",
                            amount = 20000.00,
                            currency = CurrencyValues.RUB
                    )
            }

    )
    @ScreenShotTest(value = "img/expected_stat_delete_spending.png", rewriteExpected = true)
    void checkStatComponentDeleteSpendingTest(UserJson user, BufferedImage expected) {
        final List<String> statSpendingList = List.of(
                "Развлечения 20000 ₽"
        );
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .deleteSelectedSpending(user.testData().spendings().getFirst().description())
                .shouldHaveStatSpendings(statSpendingList)
                .verifyStatDiagram(expected);
    }

    @User(
            categories = {
                    @Category(
                            name = "Обучение архив",
                            archived = true
                    )
            },
            spendings = {
                    @Spend(
                            category = "Обучение архив",
                            description = "Обучение Niffler 2.0",
                            amount = 10000.00,
                            currency = CurrencyValues.RUB
                    ),
                    @Spend(
                            category = "Развлечения",
                            description = "Обучение Niffler 3.0",
                            amount = 20000.00,
                            currency = CurrencyValues.RUB
                    )
            }

    )
    @ScreenShotTest(value = "img/expected_stat_archive_spending.png", rewriteExpected = true)
    void checkStatComponentArchiveSpendingTest(UserJson user, BufferedImage expected) {
        final List<String> statCategoriesList = List.of(
                "Развлечения 20000 ₽",
                "Archived 10000 ₽"
        );
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .shouldHaveStatSpendings(statCategoriesList)
                .verifyStatDiagram(expected);
    }
}
