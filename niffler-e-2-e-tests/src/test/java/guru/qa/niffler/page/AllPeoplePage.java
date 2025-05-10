package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {

    private final ElementsCollection allPeopleTable = $$("#all tr");
    private final SelenideElement searchField = $("input[placeholder='Search']");

    public void verifyUserOutcomeVisibleInList(String username) {
        findUser(username);
        allPeopleTable
                .findBy(text(username))
                .$(byText("Waiting..."))
                .shouldBe(visible);
    }

    public void findUser(String username) {
        searchField
                .setValue(username)
                .pressEnter();
    }
}
