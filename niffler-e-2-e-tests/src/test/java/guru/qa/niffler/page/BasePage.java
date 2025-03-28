package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class BasePage {
    private static final SelenideElement avatarButton = $(byXpath("//header//button"));
    private static final ElementsCollection menuButtons = $$(byXpath("//ul[@role='menu']//li"));

    public <T extends BasePage> T openMenu(){
        avatarButton
                .shouldBe(Condition.visible,Duration.ofSeconds(5))
                .click();
        return (T) this;
    }

    private void clickMenuButtonsExcludeLogout(int i){
        menuButtons.get(i).find(By.xpath("./a")).click();
    }

    public ProfilePage openProfilePage(){
        openMenu();
        clickMenuButtonsExcludeLogout(0);
        return new ProfilePage();
    }

    public <T extends BasePage> T assertRedirectToPage(Class<T> clazz, String... strings){
        WebDriver driver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean isAuthPage = clazz.equals(LoginPage.class) || clazz.equals(RegistrationPage.class);
        String basePage = isAuthPage? Config.getInstance().authUrl() : Config.getInstance().frontUrl();
        String expectedUrl = basePage + Pages.getUrlByClass(clazz);
        if(strings.length>0){
            for (String param : strings){
                expectedUrl+=param;
            }
        }

        try {

            wait.until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (Exception e) {

            throw new AssertionError("Не удалось попасть на страницу " + expectedUrl + " в течение 5 секунд." +
                    "\n Текущий url: "+driver.getCurrentUrl());
        }
        try {

            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AssertionError("Не удалось создать объект класса страницы.");
        }
    }
}
