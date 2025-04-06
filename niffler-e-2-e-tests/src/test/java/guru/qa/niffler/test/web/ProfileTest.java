package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.UseUser;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.web.model.User;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseWebTest {

    @Test
    @UseUser
    @Category(archived = true)
    void archivedCategoryShouldPresentInCategoriesList(User user, CategoryJson category) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .getHeader()
                .goToProfilePage()
                .clickShowArchivedToggle()
                .checkCategoryExist(category.name(), true);
    }

    @Test
    @UseUser
    @Category(archived = false)
    void activeCategoryShouldPresentInCategoriesList(User user, CategoryJson category) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .getHeader()
                .goToProfilePage()
                .checkCategoryExist(category.name(), false);
    }

}
