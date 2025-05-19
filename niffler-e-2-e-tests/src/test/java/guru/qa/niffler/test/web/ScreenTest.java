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
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;

@ExtendWith(BrowserExtension.class)

public class ScreenTest {

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
    @ScreenShootTest("img/stat_main.png")
        void screenTest(BufferedImage expectedImage) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull($("canvas[role='img']").screenshot()));
        Assertions.assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    }

}
