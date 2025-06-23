package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.PageOpenUtil.open;

@WebTest
public class CategoriesTest {

    @Test
    @User(categories = @Category())
    @ApiLogin
    void testArchiveCategory(UserJson userJson){
        String name = userJson.testData().categories().getFirst().name();
        open(ProfilePage.class)
                .showArchiveCategory()
                .assertActiveCategory(name)
                .archiveCategory(name)
                .assertArchiveToast(name)
                .assertArchiveCategory(name);
    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @ApiLogin
    void testUnarchiveCategory(UserJson userJson){
        String name = userJson.testData().categories().getFirst().name();
        open(ProfilePage.class)
                .showArchiveCategory()
                .assertArchiveCategory(name)
                .unarchiveCategory(name)
                .assertUnArchiveToast(name)
                .assertActiveCategory(name);
    }
}
