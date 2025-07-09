package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.condition.Bubble;
import guru.qa.niffler.data.condition.Color;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.InputGenerator;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class StatComponentTest extends BaseUITest {

    private static final Config CFG = Config.getInstance();
    String actualLogin = CFG.mainUserLogin();
    String actualPass = CFG.mainUserPass();


    @Test
    void twoBubblesInAnyOrderTest() throws InterruptedException {
        SpendJson firstSpend = RandomDataUtils.generateSpend(actualLogin, 500.0);
        SpendJson secondSpend = RandomDataUtils.generateSpend(actualLogin, 100.0);
        SpendJson thirdSpend = RandomDataUtils.generateSpend(actualLogin, 200.0);
        Bubble bubble1 = new Bubble(Color.yellow, InputGenerator.getExpectedBubbleText(firstSpend));
        Bubble bubble2 = new Bubble(Color.orange, InputGenerator.getExpectedBubbleText(secondSpend));
        Bubble bubble3 = new Bubble(Color.green, InputGenerator.getExpectedBubbleText(thirdSpend));
        spendDbClient.createSpend(firstSpend);
        spendDbClient.createSpend(secondSpend);
        spendDbClient.createSpend(thirdSpend);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);

        Thread.sleep(5000);

        spendDbClient.deleteTxSpend(firstSpend);
        spendDbClient.deleteTxSpend(secondSpend);
        spendDbClient.deleteTxSpend(thirdSpend);
        spendDbClient.deleteTxCategory(firstSpend.category());
        spendDbClient.deleteTxCategory(secondSpend.category());
        spendDbClient.deleteTxCategory(thirdSpend.category());
        mainPage().bubbles.checkStatBubblesInAnyOrder(bubble2, bubble3, bubble1);
    }

    @Test
    void checkBubblesContainsTest() throws InterruptedException {
        SpendJson firstSpend = RandomDataUtils.generateSpend(actualLogin, 500.0);
        SpendJson secondSpend = RandomDataUtils.generateSpend(actualLogin, 100.0);
        SpendJson thirdSpend = RandomDataUtils.generateSpend(actualLogin, 200.0);
        Bubble bubble1 = new Bubble(Color.yellow, InputGenerator.getExpectedBubbleText(firstSpend));
        Bubble bubble2 = new Bubble(Color.orange, InputGenerator.getExpectedBubbleText(secondSpend));
        spendDbClient.createSpend(firstSpend);
        spendDbClient.createSpend(secondSpend);
        spendDbClient.createSpend(thirdSpend);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);

        Thread.sleep(5000);

        spendDbClient.deleteTxSpend(firstSpend);
        spendDbClient.deleteTxSpend(secondSpend);
        spendDbClient.deleteTxSpend(thirdSpend);
        spendDbClient.deleteTxCategory(firstSpend.category());
        spendDbClient.deleteTxCategory(secondSpend.category());
        spendDbClient.deleteTxCategory(thirdSpend.category());
        mainPage().bubbles.checkContainsStatBubbles(bubble1, bubble2);
    }
}
