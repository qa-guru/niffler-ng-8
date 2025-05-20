package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import org.openqa.selenium.By;


import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.WebDriverConditions.url;


public class BasePage extends SelenideProviderService {
    private final SelenideElement avatarButton;
    private final ElementsCollection menuButtons;

    public BasePage(SelenideDriver driver){
        super(driver);
        avatarButton = $(byXpath("//header//button"));
        menuButtons = $$(byXpath("//ul[@role='menu']//li"));
    }

    public BasePage(){
        this(null);
    }

    public <T extends BasePage> T openMenu(){
        avatarButton
                .shouldBe(Condition.visible,Duration.ofSeconds(5))
                .click();
        return (T) this;
    }

    private <T extends BasePage> T clickMenuButtonsExcludeLogout(int i){
        menuButtons.get(i).find(By.xpath("./a")).click();
        return (T) this;
    }

    public ProfilePage openProfilePage(){
        openMenu()
                .clickMenuButtonsExcludeLogout(0);
        return new ProfilePage(driver);
    }

    public FriendsPage openFriendsPage(){
        openMenu()
                .clickMenuButtonsExcludeLogout(1);
        return new FriendsPage(driver);
    }

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
