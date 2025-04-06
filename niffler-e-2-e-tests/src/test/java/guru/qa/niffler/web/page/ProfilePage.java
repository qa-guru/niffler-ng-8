package guru.qa.niffler.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage {

    private final SelenideElement showArchivedToggle = $(".MuiSwitch-input");
    private final ElementsCollection categoryList = $$(".MuiGrid-grid-xs-12 > .MuiBox-root");

    public ProfilePage clickShowArchivedToggle() {
        showArchivedToggle.click();
        return this;
    }

    public ProfilePage checkCategoryExist(String categoryName, boolean isArchived) {
        SelenideElement categoryRow = categoryList
                .find(text(categoryName));
        if (isArchived) {
            categoryRow.$(".MuiChip-colorDefault").shouldBe(exist);
            categoryRow.$("button[aria-label='Unarchive category']").shouldBe(visible);
        } else {
            categoryRow.$(".MuiChip-colorPrimary").shouldBe(exist);
            categoryRow.$("button[aria-label='Archive category']").shouldBe(visible);
            categoryRow.$("button[aria-label='Edit category']").shouldBe(visible);
        }
        return this;
    }

}
