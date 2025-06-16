package guru.qa.niffler.page.component;

import guru.qa.niffler.page.component.basicComponents.LineEdit;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends LineEdit {


    public SearchField(By locator) {
        super(locator);
    }

    protected SearchField search(String value) {
        fill(value);
        $(locator).sendKeys(Keys.ENTER);
        return this;
    }
}
