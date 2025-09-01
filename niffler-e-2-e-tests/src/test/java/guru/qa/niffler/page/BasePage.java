package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.WebDriverConditions.url;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> extends SelenideProviderService {

    protected static final Config CFG = Config.getInstance();
    @Getter
    private final Header header = new Header(driver);
    private final SelenideElement alert = $(".MuiSnackbar-root");
    private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

    public BasePage(@Nullable SelenideDriver driver){
        super(driver);
    }

    public BasePage(){
        super();
    }

    public abstract String getUrl();

    @Step("assert redirect to page {strings}")
    public <B extends BasePage<?>> B assertRedirectToPage(Class<B> clazz, String... strings){
        boolean isAuthPage = clazz.equals(LoginPage.class) || clazz.equals(RegistrationPage.class);
        String basePage = isAuthPage? Config.getInstance().authUrl() : Config.getInstance().frontUrl();
        StringBuilder expectedUrl = new StringBuilder(basePage).append(Pages.getUrlByClass(clazz));
        for (String param : strings) {
            expectedUrl.append(param);
        }
        webdriver().shouldHave(url(expectedUrl.toString()));
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AssertionError("Не удалось создать объект класса страницы.");
        }
    }

    @Step("Check that alert message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(Condition.visible).should(Condition.text(expectedText));
        return (T) this;
    }

    @Step("Check that form error message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkFormErrorMessage(String... expectedText) {
        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }
}
