package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class CategoriesTest {
    private static final Config CFG = Config.getInstance();
    private static final String PASSWORD = "123";

    @User(username = "Timofey",
    categories = @Category())
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

    @User(
            username = "Timofey",
            categories = @Category(
                    archived = true
            )
    )
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
