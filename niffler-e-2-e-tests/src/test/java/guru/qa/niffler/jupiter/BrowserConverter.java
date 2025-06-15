package guru.qa.niffler.jupiter;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser browser)) {
            throw new ArgumentConversionException("Source must be a Browser enum");
        }
        return new SelenideDriver(browser.getConfig());
    }
}
