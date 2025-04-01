package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryTests {
    private static final Config CFG = Config.getInstance();

    @Test
    @Category(
            username = "ilesnikov",
            archived = false
    )
    @DisplayName("Архивация категории")
    void shouldCategoryArchiving() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("ilesnikov", "12345")
                .iconSubmit()
                .profileSubmit();
    }

    @Test
    @Category(
            username = "ilesnikov",
            archived = true
    )
    @DisplayName("Разархивация категории")
    void shouldUnzippingCategory(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("ilesnikov", "12345")
                .iconSubmit()
                .profileSubmit()
                .archivedCheckboxSubmit()
                .shouldArchivedCategory(category.name());
    }
}
