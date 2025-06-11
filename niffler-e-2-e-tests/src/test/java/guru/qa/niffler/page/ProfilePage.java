package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDifResult;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {

    private final SelenideElement archivedToggleSwitch = $("span.MuiSwitch-root");
    private final ElementsCollection categories = $$("div.MuiChip-root");
    private final SelenideElement categoriesLabel = $x("//h2[contains(text(), 'Categories')]");
    private final SelenideElement avatar = $(".MuiAvatar-img");
    private final SelenideElement uploadPictureBtn = $("input[type='file']");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");

    public ProfilePage checkProfilePageShouldBeLoaded() {
        categoriesLabel.shouldBe(visible);
        return this;
    }

    public ProfilePage clickArchiveToggleSwitch() {
        archivedToggleSwitch
                .click();
        return this;
    }

    public void verifyCategoryExists(String categoryName) {
        categories
                .findBy(text(categoryName))
                .shouldBe(visible);
    }

    public ProfilePage uploadAvatar(String path) {
        uploadPictureBtn
                .uploadFromClasspath(path);
        return this;
    }

    public ProfilePage verifyAvatar(BufferedImage expected) {
        Selenide.sleep(3000);
        BufferedImage actual;
        try {
            actual = ImageIO.read(requireNonNull(avatar.screenshot()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertFalse(new ScreenDifResult(
                expected,
                actual
        ));
        return this;
    }

    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    public ProfilePage checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(3000);
        assertFalse(
                new ScreenDiffResult(
                        avatarScreenshot(), expected
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    public BufferedImage avatarScreenshot() throws IOException {
        return ImageIO.read(requireNonNull(avatar.screenshot()));
    }

    public ProfilePage checkThatCategoryInputDisabled() {
        categoryInput.should(disabled);
        return this;
    }

    public ProfilePage submitProfile() {
        submitButton.click();
        return this;
    }

    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    public ProfilePage checkCategoryExists(String category) {
        bubbles.find(text(category)).shouldBe(visible);
        return this;
    }

    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        bubblesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    public ProfilePage checkUsername(String username) {
        this.userName.should(value(username));
        return this;
    }

    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

}
