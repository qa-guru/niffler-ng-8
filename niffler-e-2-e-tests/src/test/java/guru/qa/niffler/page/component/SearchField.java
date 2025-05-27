package guru.qa.niffler.page.component;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement clearSearchInputBtn = $("#input-clear");

    public SearchField(@Nullable SelenideDriver driver) {
        super(driver,
                driver == null
                        ? Selenide.$("input[placeholder='Search']")
                        : driver.$("input[placeholder='Search'")
        );
    }
    public SearchField() {
        super(Selenide.$("input[placeholder='Search'"));
    }

    @Nonnull
    @Step("Search data in table by value '{0}'")
    public SearchField search(String query){
        self.setValue(query)
                .pressEnter();
        return this;
    }

    @Nonnull
    @Step("Clear search field")
    public SearchField clearIfNotEmpty(){
        if (self.is(not(empty))) {
            clearSearchInputBtn.click();
            self.should(empty);
        }
        return this;
    }
}
