package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.GenCategory;
import guru.qa.niffler.jupiter.UseUser;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.web.model.User;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseWebTest {

    @Test
    @UseUser
    @GenCategory(archived = true)
    void archivedCategoryShouldPresentInCategoriesList(User user, CategoryJson category) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .getHeader()
                .goToProfile()
                .clickShowArchivedToggle()
                .checkCategoryExist(category.name(), true);
    }

    @Test
    @UseUser
    @GenCategory(archived = false)
    void activeCategoryShouldPresentInCategoriesList(User user, CategoryJson category) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .getHeader()
                .goToProfile()
                .checkCategoryExist(category.name(), false);
    }

}
