package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

public class ProfileTest extends BaseWebTest {

    @Test
    @User(
        categories = @Category(archived = true)
    )
    @ApiLogin
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        openProfilePage()
            .clickShowArchivedToggle()
            .checkCategoryExist(category.name(), true);
    }

    @Test
    @User(
        categories = @Category(archived = false)
    )
    @ApiLogin
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        openProfilePage()
            .checkCategoryExist(category.name(), false);
    }

    @Test
    @User
    @ApiLogin
    void editProfile(UserParts user) {
        String name = RandomDataUtils.genUsername();
        openProfilePage()
            .setName(name)
            .clickSaveChanges()
            .checkAllerIsSuccess("Profile successfully updated")
            .refresh()
            .checkName(name);
    }

    @User
    @ApiLogin
    @ScreenShotTest(value = "img/exp/profile/empty-avatar.png")
    void checkAvatarIsEmpty(BufferedImage expImage) {
        openProfilePage()
            .checkAvatarScreenshot(expImage);
    }

    @User
    @ApiLogin
    @ScreenShotTest(value = "img/exp/profile/save-avatar.png")
    void checkAvatarIsSave(BufferedImage expImage) {
        openProfilePage()
            .uploadAvatar("img/upload-avatar.jpeg")
            .clickSaveChanges()
            .checkAvatarScreenshot(expImage);
    }
}
