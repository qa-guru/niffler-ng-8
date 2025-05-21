package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDifResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {

    private final SelenideElement archivedToggleSwitch = $("span.MuiSwitch-root");
    private final ElementsCollection categories = $$("div.MuiChip-root");
    private final SelenideElement categoriesLabel = $x("//h2[contains(text(), 'Categories')]");
    private final SelenideElement avatar = $(".MuiAvatar-img");
    private final SelenideElement uploadPictureBtn = $("input[type='file']");

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
            actual = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertFalse(new ScreenDifResult(
                expected,
                actual
        ));
        return this;
    }


}
