package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.*;

public class AllPeoplePage {

    private final SelenideElement search = $("input");
    private final ElementsCollection allPeopleRows = $$x("//table//tr");

    public AllPeoplePage findPersonByName(String name) {
        search.sendKeys(name);
        search.sendKeys(Keys.ENTER);
        return this;
    }

    public AllPeoplePage checkTableContainsPerson(String name) {
        allPeopleRows.filter(Condition.text(name)).first().shouldBe(Condition.visible);
        return this;
    }

    public AllPeoplePage checkTableCount(int count) {
        allPeopleRows.shouldBe(CollectionCondition.size(count));
        return this;
    }


}