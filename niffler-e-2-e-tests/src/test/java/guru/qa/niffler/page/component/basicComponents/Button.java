package guru.qa.niffler.page.component.basicComponents;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import static com.codeborne.selenide.Selenide.$;

public class Button {

    private By locator;

    public Button(By locator) {
        this.locator = locator;
    }

    public Button click(){
        $(locator).scrollTo();
        $(locator).click();
        return this;
    }

    public Button jsClick(){
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        $(locator).shouldBe(Condition.visible);
        js.executeScript("arguments[0].click();", $(locator));
        return this;
    }
}
