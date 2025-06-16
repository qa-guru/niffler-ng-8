package guru.qa.niffler.page.component.basicComponents;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class Select {

    private By btnLoc;
    private By selectorLoc;

    public Select(By btnLoc, By selectorLoc) {
        this.selectorLoc = selectorLoc;
        this.btnLoc = btnLoc;
    }


    public Select choose(String variant){
        $(btnLoc).shouldBe(Condition.visible);
        $(btnLoc).scrollTo();
        ElementsCollection countriesList= $(selectorLoc).findAll(By.xpath("//li"));
        for (SelenideElement li : countriesList) {
            if (li.getText().contains(variant)) {
                li.shouldBe(Condition.visible);
                li.click();
                return this;
            }
        }
        return this;
    }
}
