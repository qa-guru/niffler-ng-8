package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.niffler.utils.PageOpenUtil.open;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

    @User
    @ScreenShotTest("expected-avatar.png")
    @ApiLogin
    void checkProfileImageTest(BufferedImage expectedProfileImage)  {
        open(ProfilePage.class)
                .uploadAvatar("avatar.png")
                .checkAvatar(expectedProfileImage);
    }

    @Test
    @User
    @ApiLogin
    void editNameTest(UserJson user){
        final String newName = randomName();

        open(ProfilePage.class)
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        new ProfilePage()
                .checkName(newName)
                .checkUsername(user.username());
    }

    @Test
    @User(
            categories = @Category()
    )
    @ApiLogin
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final String categoryName = user
                .testData()
                .categories()
                .getFirst()
                .name();

        open(ProfilePage.class)
                .assertActiveCategory(categoryName);
    }

    @Test
    @User
    @ApiLogin
    void AddNewCategoryTest() {
        String newCategory = randomCategoryName();

        open(ProfilePage.class)
                .addCategory(newCategory)
                .assertActiveCategory(newCategory);
    }

    @Test
    @User(
            categories = {
                    @Category,
                    @Category,
                    @Category,
                    @Category,
                    @Category,
                    @Category,
                    @Category,
                    @Category
            }
    )
    @ApiLogin
    void shouldForbidAddingMoreThat8Categories() {
        open(ProfilePage.class)
                .checkThatCategoryInputDisabled();
    }

}
