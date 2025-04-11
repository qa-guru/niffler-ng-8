package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
    ElementsCollection friendRows = $$("tr");

    public AllPeoplePage checkOutcomeRequestToUser(String username) {
        SelenideElement friendRow = friendRows.find(text(username));
        friendRow.find(byText("Waiting...")).shouldBe(visible);
        return this;
    }
}
