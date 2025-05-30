package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.SelenideProviderService;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Getter
public abstract class BaseComponent<T extends BaseComponent<?>> extends SelenideProviderService {


    protected final SelenideElement self;

    protected BaseComponent(@Nullable SelenideDriver driver, SelenideElement self){
        super(driver);
        this.self = self;
    }

    protected BaseComponent(SelenideElement self) {
        super();
        this.self = self;
    }
}