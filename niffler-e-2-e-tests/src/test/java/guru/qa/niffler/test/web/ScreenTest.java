package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.enums.CurrencyValues;
import guru.qa.niffler.jupiter.annotations.ScreenShootTest;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.jupiter.annotations.Spend;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.SidebarPage;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.steps.AssertionSteps;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;

@ExtendWith(BrowserExtension.class)
public class ScreenTest extends BaseTest {

    private static final Config CFG = Config.getInstance();
    String actualLogin = CFG.mainUserLogin();
    String actualPass = CFG.mainUserPass();



    @User(
            username = "test",
            spendings = @Spend(username = "test",
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB))
    @ScreenShootTest(value = "img/stats/stat_one_spend_main.png")
        void screenOneSpendTest(BufferedImage expectedImage) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        BufferedImage actualImage = new MainPage().screenshotStats();
        Assertions.assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    }

    @User(
            username = "test",
            spendings = @Spend(username = "test",
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 200,
                    currency = CurrencyValues.RUB))
    @ScreenShootTest(value = "img/stats/stat_two_spends_main.png")
        void screenTwoSpendTest(BufferedImage expectedImage) throws IOException {
        SpendJson secondSpend = RandomDataUtils.generateSpend(actualLogin, 150.0);
        spendDbClient.createSpend(secondSpend);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        BufferedImage actualImage = new MainPage().screenshotStats();
        spendDbClient.deleteTxSpend(secondSpend);
        spendDbClient.deleteTxCategory(secondSpend.category());
        AssertionSteps.assertScreenshotsEquals(expectedImage, actualImage);
    }

    @ScreenShootTest(value = "img/stats/stat_without_spends.png")
        void screenWithoutOneSpendsTest(BufferedImage expectedImage) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        BufferedImage actualImage = new MainPage().screenshotStats();
        Assertions.assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    }

    @ScreenShootTest(value = "img/expected/expected_Kiwi.png")
    void checkSetProfilePicture(BufferedImage expectedImage) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        new SidebarPage().clickMenu().clickProfile();
        new ProfilePage().uploadProfileImage(new File(kiwiPngPath))
                .clickSaveChanges();
        BufferedImage actualImage = new ProfilePage().screenshotProfileIcon();

        Assertions.assertFalse(new ScreenDiffResult(expectedImage, actualImage));

        userDbClient.clearPhotoDataByUsername(actualLogin);
    }

    @ScreenShootTest(value = "img/expected/expected_Kiwi.png")
    void checkResetProfilePicture(BufferedImage expectedImage) throws IOException {
        //Safety check
        userDbClient.clearPhotoDataByUsername(actualLogin);
        //Act
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        new SidebarPage().clickMenu().clickProfile();
        //Загружаем обезьянку
        new ProfilePage().uploadProfileImage(new File(monkeyPngPath))
                .clickSaveChanges();
        Selenide.open(CFG.frontUrl() + "main", SidebarPage.class)
                .clickMenu().clickProfile();
        //Скриншотим обезбянку
        BufferedImage actualMonkey = new ProfilePage().screenshotProfileIcon();
        //Проверяем что обезбянка реалбно
        Assertions.assertFalse(new ScreenDiffResult(ImageIO.read(
                new File("C:\\niffler-ng-81\\niffler-e-2-e-tests\\src\\test\\resources" +
                        "\\img\\expected\\expected_monkey.png")), actualMonkey));
        //Меняем обезьянку на Киви   (Киви это мой кот :) )
        new ProfilePage().uploadProfileImage(new File(kiwiPngPath))
                .clickSaveChanges();
        //Скриншотим Киви
        BufferedImage actualKiwi = new ProfilePage().screenshotProfileIcon();
        //Проверяем что Киви реально
        Assertions.assertFalse(new ScreenDiffResult(expectedImage, actualKiwi));
        //Cleanup
        userDbClient.clearPhotoDataByUsername(actualLogin);
    }

}
