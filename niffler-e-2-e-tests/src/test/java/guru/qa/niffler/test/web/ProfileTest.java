package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.UserParts;
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
    void archivedCategoryShouldPresentInCategoriesList(UserParts user, CategoryJson category) {
        openProfilePage(user)
            .clickShowArchivedToggle()
            .checkCategoryExist(category.name(), true);
    }

    @Test
    @User(
        categories = @Category(archived = false)
    )
    void activeCategoryShouldPresentInCategoriesList(UserParts user, CategoryJson category) {
        openProfilePage(user)
            .checkCategoryExist(category.name(), false);
    }

    @Test
    @User
    void editProfile(UserParts user) {
        String name = RandomDataUtils.genUsername();
        openProfilePage(user)
            .setName(name)
            .clickSaveChanges()
            .checkAllerIsSuccess("Profile successfully updated")
            .refresh()
            .checkName(name);
    }

    @User
    @ScreenShotTest(value = "img/exp/profile/empty-avatar.png")
    void checkAvatarIsEmpty(UserParts user, BufferedImage expImage) {
        openProfilePage(user)
            .checkAvatarScreenshot(expImage);
    }

    @User
    @ScreenShotTest(value = "img/exp/profile/save-avatar.png")
    void checkAvatarIsSave(UserParts user, BufferedImage expImage) {
        openProfilePage(user)
            .uploadAvatar("img/upload-avatar.jpeg")
            .clickSaveChanges()
            .checkAvatarScreenshot(expImage);
    }
}
