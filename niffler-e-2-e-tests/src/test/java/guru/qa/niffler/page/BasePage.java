package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.WebDriverConditions.url;

@ParametersAreNonnullByDefault
public class BasePage extends SelenideProviderService {

    @Getter
    private final Header header = new Header(driver);

    public BasePage(@Nullable SelenideDriver driver){
        super(driver);
    }

    public BasePage(){
        this(null);
    }

    @Step("assert redirect to page {strings}")
    public <T extends BasePage> T assertRedirectToPage(Class<T> clazz, String... strings){
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
}
