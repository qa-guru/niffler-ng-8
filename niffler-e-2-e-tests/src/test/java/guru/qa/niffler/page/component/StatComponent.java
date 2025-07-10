package guru.qa.niffler.page.component;

import guru.qa.niffler.data.condition.Bubble;
import guru.qa.niffler.data.condition.Color;
import guru.qa.niffler.utils.CommonSteps;
import org.openqa.selenium.By;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.niffler.data.condition.StatConditions.*;

public class StatComponent extends BaseComponent {

    protected By locator;

    public StatComponent(By locator) {
        this.locator = locator;
    }

    public BufferedImage screenshotStats() throws IOException {
        sleep(5000);
        return CommonSteps.screenshot($(locator));
    }

    public StatComponent checkBubbles(Color... expectedColors) {
        $(locator).$$("li").should(color(expectedColors));

        return this;
    }

    public StatComponent checkBubbles(Bubble... bubbles) {
        $(locator).$$("li").should(shouldHaveBubbles(bubbles));
        return this;
    }

    public StatComponent checkStatBubblesInAnyOrder(Bubble... bubbles) {
        $(locator).$$("li").should(shouldHaveStatBubblesInAnyOrder(bubbles));
        return this;
    }

    public StatComponent checkContainsStatBubbles(Bubble... bubbles) {
        $(locator).$$("li").should(shouldContainsStatBubbles(bubbles));
        return this;
    }
}
