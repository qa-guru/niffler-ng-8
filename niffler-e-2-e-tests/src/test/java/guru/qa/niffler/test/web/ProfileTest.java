package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseWebTest {

    @Test
    @User(
            categories = @Category(archived = true)
    )
    void archivedCategoryShouldPresentInCategoriesList(WebUser user, CategoryJson category) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .getHeader()
                .goToProfilePage()
                .clickShowArchivedToggle()
                .checkCategoryExist(category.name(), true);
    }

    @Test
    @User(
            categories = @Category(archived = false)
    )
    void activeCategoryShouldPresentInCategoriesList(WebUser user, CategoryJson category) {
        openLoginPage()
                .doLoginSuccess(user.username(), user.password())
                .getHeader()
                .goToProfilePage()
                .checkCategoryExist(category.name(), false);
    }

}
