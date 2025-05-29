package guru.qa.niffler.page.component.basicComponents;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;


public class Table {


    protected final By table = By.xpath("table");
    protected final By rows = By.xpath("tr");


    protected SelenideElement findRowByText(String text) {
            return $("//table//tr//*[contains(text(), '" + text + "')]//ancestor::tr");
    }

    public Table checkTableSize(int count) {
        $(table).$$(rows).shouldBe(CollectionCondition.size(count));
        return this;
    }

    public Table checkTableEmpty() {
        checkTableSize(0);
        return this;
    }

    public Table checkTableVisible() {
        $(table).shouldBe(Condition.visible);
        return this;
    }

    public Table checkTableNotVisible() {
        $(table).shouldNotBe(Condition.visible);
        return this;
    }
}
