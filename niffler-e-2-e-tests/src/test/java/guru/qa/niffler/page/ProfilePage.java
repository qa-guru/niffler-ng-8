package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;


import java.time.Duration;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfilePage extends BasePage {
    private final SelenideElement avatarImage = $("main img");
    private final SelenideElement uploadAvatarButton = $("label[for='image__input']");
    private final SelenideElement nameInput = $("input[id='name']");
    private final SelenideElement usernameInput = $("input[id='username']");
    private final SelenideElement saveNameButton = $("button[id=':r7:']");
    private final SelenideElement categoryStatusSwitcher = $("input[type='checkbox']");
    private final SelenideElement newCategoryInput = $("input[placeholder='Add new category']");
    private final ElementsCollection categoryLabels = $$(".MuiChip-label");
    private final SelenideElement unarchiveButton = $$("button.MuiButton-containedPrimary")
            .findBy(text("Unarchive"));
    private final SelenideElement archiveButton = $$("button.MuiButton-containedPrimary")
            .findBy(text("Archive"));



    private SelenideElement getCategoryButton(String name) {
        return categoryLabels.find(text(name));
    }

    private SelenideElement getCategoryRow(String name) {
        return getCategoryButton(name).parent().parent();
    }

    private SelenideElement getCategoryEditIcon(String name) {
        return getCategoryRow(name).find("button[aria-label='Edit category']");
    }

    private SelenideElement getCategoryArchiveIcon(String name) {
        return getCategoryRow(name)
                .find("button[aria-label='Archive category']");
    }

    private SelenideElement getCategoryUnarchiveIcon(String name) {
        return getCategoryRow(name)
                .find("button[aria-label='Unarchive category']");
    }

    private SelenideElement getCategoryEditInput(String name) {
        return $(String.format("input[placeholder='Edit category' and value='%s']", name));
    }

    private SelenideElement getCloseCategoryEditInputButton(String name) {
        return getCategoryEditInput(name).parent().find("button");
    }

    public ProfilePage archiveCategory(String name){
        Selenide.executeJavaScript("arguments[0].click();",getCategoryArchiveIcon(name));
        archiveButton.shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        archiveButton.shouldNot(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    public ProfilePage unarchiveCategory(String name){
        Selenide.executeJavaScript("arguments[0].click();",getCategoryUnarchiveIcon(name));
        unarchiveButton.shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        unarchiveButton.shouldNot(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    public ProfilePage assertArchiveCategory(String name){
        getCategoryButton(name)
                .shouldBe(Condition.visible,Duration.ofSeconds(5))
                .scrollTo();
        getCategoryButton(name)
                .parent()
                .shouldHave(cssValue("background-color", "rgba(234, 236, 250, 1)"));
        assertFalse(getCategoryEditIcon(name).exists());
        return this;
    }

    public ProfilePage assertArchiveToast(String name){
        String toastText = String.format("Category %s is archived", name);
        $(byText(toastText)).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public ProfilePage assertActiveCategory(String name){
        getCategoryButton(name)
                .shouldBe(Condition.visible,Duration.ofSeconds(5))
                .scrollTo();
        getCategoryButton(name)
                .parent()
                .shouldHave(cssValue("background-color", "rgba(41, 65, 204, 1)"));
        assertTrue(getCategoryEditIcon(name).exists());
        return this;
    }

    public ProfilePage assertUnArchiveToast(String name){
        String toastText = String.format("Category %s is unarchived", name);
        $(byText(toastText)).shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public ProfilePage showArchiveCategory(){
        categoryStatusSwitcher
                .shouldBe(Condition.exist,Duration.ofSeconds(5))
                .scrollTo()
                .click();
        return this;
    }
}
