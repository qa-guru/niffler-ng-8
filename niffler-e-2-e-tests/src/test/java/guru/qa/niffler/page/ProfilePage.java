package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.openqa.selenium.By;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfilePage extends BasePage {

    private final SelenideElement avatarImage;
    private final SelenideElement uploadAvatarButton;
    private final SelenideElement pictureInput;
    private final SelenideElement nameInput;
    private final SelenideElement usernameInput;
    private final SelenideElement saveNameButton;
    private final SelenideElement categoryStatusSwitcher;
    private final SelenideElement newCategoryInput;
    private final ElementsCollection categoryLabels;
    private final SelenideElement unarchiveButton;
    private final SelenideElement archiveButton;

    public ProfilePage(SelenideDriver driver) {
        super(driver);
        this.avatarImage = $("main img");
        this.uploadAvatarButton = $("label[for='image__input']");
        this.pictureInput = $("input[type='file']");
        this.nameInput = $("input[id='name']");
        this.usernameInput = $("input[id='username']");
        this.saveNameButton = $("button[id=':r7:']");
        this.categoryStatusSwitcher = $("input[type='checkbox']");
        this.newCategoryInput = $("input[placeholder='Add new category']");
        this.categoryLabels = $$(".MuiChip-label");
        this.unarchiveButton = $$("button.MuiButton-containedPrimary").findBy(text("Unarchive"));
        this.archiveButton = $$("button.MuiButton-containedPrimary").findBy(text("Archive"));
    }

    public ProfilePage() {
        this(null);
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

    public ProfilePage unarchiveCategory(String name){
        if(driver == null) {
            Selenide.executeJavaScript("arguments[0].click();", getCategoryArchiveIcon(name));
        } else {
            driver.executeJavaScript("arguments[0].click();", getCategoryArchiveIcon(name));
        }
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
        By toastBy = byText(String.format("Category %s is archived", name));
        final SelenideElement toast = driver == null
                ? $(toastBy)
                : driver.$(toastBy);
        toast.shouldBe(Condition.visible,Duration.ofSeconds(5));
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
        By toastBy = byText(String.format("Category %s is unarchived", name));
        final SelenideElement toast = driver == null
                ? $(toastBy)
                : driver.$(toastBy);
        toast.shouldBe(Condition.visible,Duration.ofSeconds(5));
        return this;
    }

    public ProfilePage showArchiveCategory(){
        categoryStatusSwitcher
                .shouldBe(Condition.exist,Duration.ofSeconds(5))
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
}
