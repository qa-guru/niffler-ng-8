package guru.qa.niffler.test.web;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseWebTest {

    @Test
    @User(
            categories = @Category(archived = true)
    )
    void archivedCategoryShouldPresentInCategoriesList(UserParts user, CategoryJson category) {
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .clickShowArchivedToggle()
                .checkCategoryExist(category.name(), true);
    }

    @Test
    @User(
            categories = @Category(archived = false)
    )
    void activeCategoryShouldPresentInCategoriesList(UserParts user, CategoryJson category) {
        openLoginPage()
            .doLoginSuccess(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .checkCategoryExist(category.name(), false);
    }

}
