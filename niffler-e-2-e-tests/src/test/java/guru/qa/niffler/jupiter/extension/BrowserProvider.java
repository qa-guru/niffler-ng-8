package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.utils.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserProvider implements ArgumentConverter {
    @Override
    public SelenideDriver convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser)) {
            throw new ArgumentConversionException("Cannot convert argument");
        }
        try {
            Browser browser = (Browser) source;
            SelenideDriver driver = new SelenideDriver(browser.config());
            NonStaticBrowserExtension.add(driver);
            return driver;
        } catch (IllegalArgumentException e){
            throw new ArgumentConversionException("Failed to convert argument to Browser enum: " + source, e);
        }
    }
}
