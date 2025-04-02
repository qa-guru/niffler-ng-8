package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage {

    private final SelenideElement inputImage = $("input[id='image__input']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement archivedToggle = $("input[type='checkbox']");
    private final SelenideElement newCategoryInput = $("input[id='category']");
    private final ElementsCollection categoryChips = $$("span.MuiChip-label");
    private final SelenideElement unarchiveSubmitBtn = $$("button.MuiButton-containedPrimary")
            .findBy(text("Unarchive"));
    private final SelenideElement archiveSubmitBtn = $$("button.MuiButton-containedPrimary")
            .findBy(text("Archive"));
    private final SelenideElement editCategoryInput = $("input[name='category'][placeholder='Edit category']");

    public ProfilePage editUsernameIsNotAvailable(String expectedUsername) {
        usernameInput.shouldHave(value(expectedUsername), attribute("disabled"));
        return this;
    }

    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    public ProfilePage clickSaveChanges() {
        submitBtn.click();
        return this;
    }

    public ProfilePage updateProfilePicture(String filePath) {
        inputImage.$("input[type='file']").uploadFromClasspath(filePath);
        return this;
    }

    public ProfilePage switchArchivetToggle() {
        archivedToggle.click();
        return this;
    }

    public ProfilePage addNewCategory(String newCategory) {
        newCategoryInput.setValue(newCategory).pressEnter();
        return this;
    }

    public ProfilePage checkCategoryExists(String category) {
        categoryChips.stream().anyMatch(
                element -> element.shouldBe(visible).has(text(category)));
        return this;
    }

    public SelenideElement getCategoryContainer(String categoryName) {
        return $x("//span[contains(@class, 'MuiChip-label') and text()='" +
                          categoryName +
                          "']/ancestor::div[contains(@class, 'MuiBox-root')]");
    }

    public ProfilePage editCategory(String category, String newCategoryName) {
        getCategoryContainer(category).$("button[aria-label='Edit category']").click();
        editCategoryInput.clear();
        editCategoryInput.setValue(newCategoryName).pressEnter();
        return this;
    }

    public ProfilePage archiveCategory(String category) {
        getCategoryContainer(category).$("\"button[aria-label='Archive category']\"").click();
        archiveSubmitBtn.click();
        return this;
    }

    public ProfilePage unarchiveCategory(String category) {
        getCategoryContainer(category).$("\"button[aria-label='Unarchive category']\"").click();
        unarchiveSubmitBtn.click();
        return this;
    }
}
