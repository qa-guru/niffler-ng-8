package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;


import static com.codeborne.selenide.Selenide.*;


public class BasePage {

    protected final String errorBaseLocator = "//span[contains(text(), '%s')] | //p[contains(text(), '%s')]";
    protected final SelenideElement menuButton = $x("//button[@aria-label='Menu']");
    protected final ElementsCollection menuOptions = $$x("//li[@role = 'menuitem']");

    public void assertErrorShown(String errorText) {
        $x(String.format(errorBaseLocator, errorText, errorText)).shouldBe(Condition.visible.because("Должна отобразиться ошибка :: " + errorText));
    }


    public <T extends BasePage> T openPageFromMenu(String menuOption, Class<T> pageClass) {
        openMenu();
        choseMenuOption(menuOption);
        try {
            return pageClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create page instance", e);
        }
    }

    public <T extends BasePage> T openMenu() {
        menuButton.click();
        return (T) this;
    }

    private void choseMenuOption(String menuOption){
        menuOptions.find(Condition.text(menuOption)).click();
    }
}
