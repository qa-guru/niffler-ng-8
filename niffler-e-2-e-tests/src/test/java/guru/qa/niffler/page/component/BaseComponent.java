package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public abstract class BaseComponent<T extends BaseComponent<T>> {

    private By locator;

    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkVisible() {
        $(locator).shouldBe((Condition.visible));
        return (T) this;
    }
}
