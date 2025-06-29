package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

public abstract class BaseComponent<P, C extends BaseComponent<P, C>> {

    @Getter
    protected final SelenideElement self;
    protected final P page;

    public BaseComponent(P currentPage, SelenideElement self) {
        this.self = self;
        this.page = currentPage;
    }

    public P returnToPage() {
        return page;
    }
}
