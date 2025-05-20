package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collection;

public abstract class SelenideProviderService {
    protected final SelenideDriver driver;

    public SelenideProviderService(SelenideDriver driver) {
        this.driver = driver;
        if (driver != null) {
            NonStaticBrowserExtension.add(driver);
        }
    }

    public SelenideDriver driver(){
        return this.driver;
    }

    
    public SelenideElement $(WebElement webElement) {
        return driver == null
                ? Selenide.$(webElement)
                : driver.$(webElement);
    }

    
    public SelenideElement $(String cssSelector) {
        return driver == null
                ? Selenide.$(cssSelector)
                : driver.$(cssSelector);
    }

    
    public SelenideElement $x(String xpathExpression) {
        return driver == null
                ? Selenide.$x(xpathExpression)
                : driver.$x(xpathExpression);
    }

    
    public SelenideElement $(By seleniumSelector) {
        return driver == null
                ? Selenide.$(seleniumSelector)
                : driver.$(seleniumSelector);
    }

    
    public SelenideElement $(By seleniumSelector, int index) {
        return driver == null
                ? Selenide.$(seleniumSelector, index)
                : driver.$(seleniumSelector, index);
    }

    
    public SelenideElement $(String cssSelector, int index) {
        return driver == null
                ? Selenide.$(cssSelector, index)
                : driver.$(cssSelector, index);
    }

    
    public ElementsCollection $$(Collection<? extends WebElement> elements) {
        return driver == null
                ? Selenide.$$(elements)
                : driver.$$(elements);
    }

    
    public ElementsCollection $$(String cssSelector) {
        return driver == null
                ? Selenide.$$(cssSelector)
                : driver.$$(cssSelector);
    }

    
    public ElementsCollection $$x(String xpathExpression) {
        return driver == null
                ? Selenide.$$x(xpathExpression)
                : driver.$$x(xpathExpression);
    }

    
    public ElementsCollection findAll(By seleniumSelector) {
        return driver == null
                ? Selenide.getFocusedElement().findAll(seleniumSelector)
                : driver.findAll(seleniumSelector);
    }

    
    public ElementsCollection findAll(String cssSelector) {
        return driver == null
                ? Selenide.getFocusedElement().findAll(cssSelector)
                : driver.findAll(cssSelector);
    }

    
    public ElementsCollection $$(By criteria) {
        return driver == null
                ? Selenide.$$(criteria)
                : driver.$$(criteria);
    }

    public Conditional<WebDriver> webdriver() {
        return driver == null
                ? Selenide.webdriver()
                : driver.webdriver();
    }
}
