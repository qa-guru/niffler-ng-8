package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {

    private final SelenideElement archivedToggleSwitch = $("span.MuiSwitch-root");
    private final ElementsCollection categories = $$("div.MuiChip-root");
    private final SelenideElement categoriesLabel = $x("//h2[contains(text(), 'Categories')]");

    public ProfilePage checkProfilePageShouldBeLoaded() {
        categoriesLabel.shouldBe(visible);
        return this;
    }

    public ProfilePage clickArchiveToggleSwitch() {
        archivedToggleSwitch
                .shouldBe(visible)
                .click();
        return this;
    }

    public void verifyCategoryExists(String categoryName) {
        categories
                .findBy(text(categoryName))
                .shouldBe(visible);
    }


}
