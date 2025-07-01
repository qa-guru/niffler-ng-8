package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;

public class SearchField<P> extends BaseComponent<P, SearchField<P>> {

    private final SelenideElement input = self.$("input");
    private final SelenideElement submitButton = self.$("button[type='submit']");

    public SearchField(P currentPage, SelenideElement self) {
        super(currentPage, self);
    }

    public SearchField<P> search(String query) {
        clearIfNotEmpty(query);
        input.setValue(query);
        submitButton.click();
        return this;
    }

    public SearchField<P> clearIfNotEmpty(String query) {
        if (!input.getValue().isBlank()) {
            input.clear();
        }
        return this;
    }
}
