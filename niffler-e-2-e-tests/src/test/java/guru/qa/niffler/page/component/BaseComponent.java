package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.SelenideProviderService;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public abstract class BaseComponent<T extends BaseComponent<?>> extends SelenideProviderService {

    @Nonnull
    protected final SelenideElement self;

    protected BaseComponent(SelenideDriver driver, @NotNull SelenideElement self){
        super(driver);
        this.self = self;
    }

    protected BaseComponent(SelenideElement self) {
        this(null,self);
    }

    @Nonnull
    public SelenideElement getSelf() {
        return self;
    }
}