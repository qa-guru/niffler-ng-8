package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class CategoriesTest {
    private static final Config CFG = Config.getInstance();
    private static final String PASSWORD = "123";

    @Category(
            username = "Timofey",
            archived = false)
    @Test
    void testArchiveCategory(CategoryJson categoryJson){
        String name = categoryJson.name();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(categoryJson.username(), PASSWORD)
                .openProfilePage()
                .showArchiveCategory()
                .assertActiveCategory(name)
                .archiveCategory(name)
                .assertArchiveToast(name)
                .assertArchiveCategory(name);
    }

    @Category(
            username = "Timofey",
            archived = true)
    @Test
    void testUnarchiveCategory(CategoryJson categoryJson){
        String name = categoryJson.name();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(categoryJson.username(), PASSWORD)
                .openProfilePage()
                .showArchiveCategory()
                .assertArchiveCategory(name)
                .unarchiveCategory(name)
                .assertUnArchiveToast(name)
                .assertActiveCategory(name);
    }
}
