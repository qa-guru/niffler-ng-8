package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {

        if (!(source instanceof Browser)) {
            throw new ArgumentConversionException("Source must be enum");
        }
        return new SelenideDriver(((Browser) source).config());
    }
}
