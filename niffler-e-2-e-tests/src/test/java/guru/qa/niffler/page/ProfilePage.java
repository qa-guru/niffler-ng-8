package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {

    private final SelenideElement profileIcon = $x("//*[@id='image__input']/following-sibling::[@data-testid='PersonIcon']");
    private final SelenideElement imageInput = $x("//*[@id='image__input']");
    private final SelenideElement userName = $("#username");
    private final SelenideElement name = $("#name");
    private final SelenideElement saveChanges = $("#:r1:");
    private final SelenideElement showArchived = $x("//*[contains(@class, 'MuiSwitch-switchBase')]");
    private final SelenideElement newCategory = $("#category");

    private final ElementsCollection categoriesTable = $$x("//*[contains(@class, 'css-1lekzkb')]");
    private final SelenideElement edit = $x("//*[@aria-label='Edit category']");
    private final SelenideElement archive = $x("//*[@aria-label='Archive category']");


    public ProfilePage setUsername(String username) {
        userName.sendKeys(username);
        return this;
    }

    public ProfilePage setName(String name) {
        this.name.sendKeys(name);
        return this;
    }

    public ProfilePage clickSave() {
        saveChanges.click();
        return this;
    }

    private Boolean isArchiveShowed() {
        return showArchived.has(Condition.attribute("class", "Mui-checked"));
    }

    public ProfilePage showArchive() {
        if (isArchiveShowed()) {
            return this;
        } else showArchived.click();
        return this;
    }

    public ProfilePage hideArchive() {
        showArchived.is(Condition.exist);
        showArchived.scrollTo();
        if (!isArchiveShowed()) {
            return this;
        } else showArchived.click();
        return this;
    }

    public ProfilePage setNewCategory(String category) {
        showArchived.scrollTo();
        newCategory.sendKeys(category);
        return this;
    }

    public ProfilePage checkTableContainsCategory(String name) {
        categoriesTable.filter(Condition.text(name)).first().shouldBe(Condition.exist);
        return this;
    }

    public ProfilePage checkCategoryNotExistInTable(String name) {
        categoriesTable.filter(Condition.text(name)).first().shouldNotBe(Condition.exist);
        return this;
    }
}
