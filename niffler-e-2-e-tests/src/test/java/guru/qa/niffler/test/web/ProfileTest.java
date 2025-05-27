package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User
    @ScreenShotTest(value = "img/expected-avatar.png")
    void checkProfileImageTest(UserJson user, BufferedImage expectedProfileImage)  {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .getHeader()
                .toProfilePage()
                .uploadAvatar("img/avatar.png")
                .checkAvatar(expectedProfileImage);
    }

    @Test
    @User
    void editNameTest(UserJson user){
        final String newName = randomName();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .getHeader()
                .toProfilePage()
                .setName(newName);

        Selenide.refresh();

        new ProfilePage()
                .checkName(newName)
                .checkUsername(user.username());
    }

    @Test
    @User(
            categories = @Category()
    )
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final String categoryName = user
                .testData()
                .categories()
                .getFirst()
                .name();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .getHeader()
                .toProfilePage()
                .assertActiveCategory(categoryName);
    }

    @Test
    @User
    void AddNewCategoryTest(UserJson user) {
        String newCategory = randomCategoryName();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .getHeader()
                .toProfilePage()
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
    void shouldForbidAddingMoreThat8Categories(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .getHeader()
                .toProfilePage()
                .checkThatCategoryInputDisabled();
    }

}
