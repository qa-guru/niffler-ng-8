package guru.qa.niffler.page.component;

import guru.qa.niffler.utils.RandomDataUtils;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

public class CategoryPicker {

    private By loc;

    public CategoryPicker(By locator) {
        this.loc = locator;
    }


    public CategoryPicker pickRandomCategory(){
            int randomNum = RandomDataUtils.getRandomNumberInRange(0, getSizeSelect());
            $(loc).$(By.xpath("./li[" + randomNum + "]")).should(exist).click();
            return this;
        }

    private int getSizeSelect() {
        $(loc).$(By.xpath("./li")).should(exist);
        return $(loc).$$(By.xpath("./li")).size();
    }
}
