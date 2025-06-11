package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.openqa.selenium.By;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    private final SelenideElement avatarImage  = $("main img");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement pictureInput = $("input[type='file']");
    private final SelenideElement nameInput = $("input[id='name']");
    private final SelenideElement usernameInput = $("input[id='username']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryStatusSwitcher = $("input[type='checkbox']");
    private final ElementsCollection categoryLabels = $$(".MuiChip-label");
    private final SelenideElement unarchiveButton = $$("button.MuiButton-containedPrimary").findBy(text("Unarchive"));
    private final SelenideElement archiveButton = $$("button.MuiButton-containedPrimary").findBy(text("Archive"));

    public ProfilePage(@Nullable SelenideDriver driver) {
        super(driver);
    }

    @Step("Save profile")
    @Nonnull
    public ProfilePage submitProfile() {
        submitButton.click();
        return this;
    }


    public ProfilePage() {
        super();
    }

    @Override
    public String getUrl() {
        return "profile";
    }

    @Step("Set category: '{0}'")
    @Nonnull
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Step("Set name: '{0}'")
    @Nonnull
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }


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
        String selector = String.format("input[placeholder='Edit category' and value='%s']", name);
        return driver == null
                ? $(selector)
                : driver.$(selector);
    }

    private SelenideElement getCloseCategoryEditInputButton(String name) {
        return getCategoryEditInput(name).parent().find("button");
    }

    @Step("Archive category {name}")
    public ProfilePage archiveCategory(String name){
        if(driver == null) {
            Selenide.executeJavaScript("arguments[0].click();", getCategoryArchiveIcon(name));
        } else {
            driver.executeJavaScript("arguments[0].click();", getCategoryArchiveIcon(name));
        }
        archiveButton.shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        archiveButton.shouldNot(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Unarchive category '{name}'")
    public ProfilePage unarchiveCategory(String name) {
        if (driver == null) {
            Selenide.executeJavaScript("arguments[0].click();", getCategoryArchiveIcon(name));
        } else {
            driver.executeJavaScript("arguments[0].click();", getCategoryArchiveIcon(name));
        }
        unarchiveButton.shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        unarchiveButton.shouldNot(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify category '{name}' is archived")
    public ProfilePage assertArchiveCategory(String name) {
        getCategoryButton(name)
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .scrollTo();
        getCategoryButton(name)
                .parent()
                .shouldHave(cssValue("background-color", "rgba(234, 236, 250, 1)"));
        assertFalse(getCategoryEditIcon(name).exists());
        return this;
    }

    @Step("Verify archive toast appears for category '{name}'")
    public ProfilePage assertArchiveToast(String name) {
        By toastBy = byText(String.format("Category %s is archived", name));
        final SelenideElement toast = driver == null
                ? $(toastBy)
                : driver.$(toastBy);
        toast.shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Verify category '{name}' is active")
    public ProfilePage assertActiveCategory(String name) {
        getCategoryButton(name)
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .scrollTo();
        getCategoryButton(name)
                .parent()
                .shouldHave(cssValue("background-color", "rgba(41, 65, 204, 1)"));
        assertTrue(getCategoryEditIcon(name).exists());
        return this;
    }

    @Step("Verify unarchive toast appears for category '{name}'")
    public ProfilePage assertUnArchiveToast(String name) {
        By toastBy = byText(String.format("Category %s is unarchived", name));
        final SelenideElement toast = driver == null
                ? $(toastBy)
                : driver.$(toastBy);
        toast.shouldBe(Condition.visible, Duration.ofSeconds(5));
        return this;
    }

    @Step("Show archived categories")
    public ProfilePage showArchiveCategory() {
        categoryStatusSwitcher
                .shouldBe(Condition.exist, Duration.ofSeconds(5))
                .scrollTo()
                .click();
        return this;
    }

    @SneakyThrows
    @Step("Check profile avatar")
    public ProfilePage checkAvatar(BufferedImage expected) {
        Selenide.sleep(3000);
        BufferedImage actual = ImageIO.read(avatarImage.screenshot());
        assertFalse(new ScreenDiffResult(expected, actual));
        return this;
    }

    @SneakyThrows
    @Step("Upload avatar")
    public ProfilePage uploadAvatar(String path) {
        pictureInput.uploadFromClasspath(path);
        return this;
    }
    @Step("Check userName: '{0}'")
    @Nonnull
    public ProfilePage checkUsername(String username) {
        this.usernameInput.should(value(username));
        return this;
    }

    @Step("Check name: '{0}'")
    @Nonnull
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Step("Check that category input is disabled")
    @Nonnull
    public ProfilePage checkThatCategoryInputDisabled() {
        categoryInput.should(disabled);
        return this;
    }
}
