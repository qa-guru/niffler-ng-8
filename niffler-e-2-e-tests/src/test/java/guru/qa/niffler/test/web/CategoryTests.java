package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryTests {
    private static final Config CFG = Config.getInstance();

    @Test
    @User(
        username = "ilesnikov",
        categories = @Category(
            archived = false
        )
    )
    @DisplayName("Архивация категории")
    void shouldCategoryArchiving(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin("ilesnikov", "12345")
            .iconSubmit()
            .profileSubmit()
            .archivedCategorySubmit(category.name())
            .buttonSubmit("Archive")
            .archivedCheckboxSubmit()
            .shouldArchivedCategoryDisplayed(category.name());
    }

    @Test
    @User(
        username = "ilesnikov",
        categories = @Category(
            archived = true
        ))
    @DisplayName("Разархивация категории")
    void shouldUnzippingCategory(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin("ilesnikov", "12345")
            .iconSubmit()
            .profileSubmit()
            .archivedCheckboxSubmit()
            .unarchivedCategorySubmit(category.name())
            .buttonSubmit("Unarchive")
            .shouldArchiveCategoryButtonDisplayed(category.name());
    }
}
