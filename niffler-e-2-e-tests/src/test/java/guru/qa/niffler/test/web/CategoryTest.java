package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class CategoryTest extends BaseTest {

    @Category(
            username = "user1",
            archived = false
    )
    @Test
    void activeCategoryShouldBePresentInCategoriesList(CategoryJson categoryJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("user1", "user1")
                .openPageFromMenu("Profile", ProfilePage.class)
                .assertCategoryIsShown(categoryJson);
    }

    @Category(
            username = "user1",
            archived = true)
    @Test
    void archivedCategoryShouldBePresentInCategoriesList(CategoryJson categoryJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("user1", "user1")
                .openPageFromMenu("Profile", ProfilePage.class)
                .assertCategoryIsShown(categoryJson);
    }

}
