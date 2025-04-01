package guru.qa.niffler.web.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.web.element.Category;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage {

    private final SelenideElement showArchivedToggle = $(".MuiSwitch-input");

    private List<Category> getCategories() {
        return initElements($$(".MuiGrid-grid-xs-12 > .MuiBox-root"), Category.class);
    }

    public ProfilePage clickShowArchivedToggle() {
        showArchivedToggle.click();
        return this;
    }

    public ProfilePage checkCategoryExist(String categoryName, boolean isArchived) {
        Optional<Category> findCategory = getCategories().stream()
                .filter(c -> c.getName().equals(categoryName))
                .findFirst();
        if (findCategory.isEmpty()) {
            Assertions.fail("Отсутствует категория '%s'".formatted(categoryName));
        } else {
            Assertions.assertEquals(isArchived, findCategory.get().isArchival(),
                    "Категория '%s' ожидается isArchival=%s".formatted(categoryName, isArchived));
        }
        return this;
    }

}
