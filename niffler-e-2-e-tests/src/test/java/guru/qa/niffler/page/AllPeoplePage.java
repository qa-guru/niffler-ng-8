package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {

    private static final ElementsCollection allPeopleTable = $$("#all tr");

    public void verifyUserOutcomeVisibleInList(String username) {
        allPeopleTable
                .findBy(text(username))
                .$(byText("Waiting..."))
                .shouldBe(visible);
    }
}
