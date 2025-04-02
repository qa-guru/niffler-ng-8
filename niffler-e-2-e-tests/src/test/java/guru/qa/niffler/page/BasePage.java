package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import org.openqa.selenium.By;


import java.time.Duration;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;

public class BasePage {
    private static final SelenideElement avatarButton = $(byXpath("//header//button"));
    private static final ElementsCollection menuButtons = $$(byXpath("//ul[@role='menu']//li"));

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
        return new ProfilePage();
    }

    public FriendsPage openFriendsPage(){
        openMenu()
                .clickMenuButtonsExcludeLogout(1);
        return new FriendsPage();
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
