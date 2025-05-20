package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Browsers;
import guru.qa.niffler.utils.Browser;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension.getDrivers;

public class NonStaticBrowsersResolverExtension implements
        BeforeEachCallback,
        ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(NonStaticBrowsersResolverExtension.class);
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Browsers.class)
                .ifPresent(anno -> {
                    Browser[] browsers = anno.value();
                    if(browsers.length == 0){
                        NonStaticBrowserExtension.add(new SelenideDriver(Browser.CHROME.config()));
                    } else {
                        Arrays.stream(browsers)
                                .map(browser -> new SelenideDriver(browser.config()))
                                .forEach(NonStaticBrowserExtension::add);
                    }
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            getDrivers()
                    );
                });
    }



    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(SelenideDriver[].class);
    }

    @Override
    public SelenideDriver[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        @SuppressWarnings("unchecked")
        Set<SelenideDriver> drivers = (Set<SelenideDriver>) extensionContext
                .getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Set.class);

        if (drivers == null) {
            throw new ParameterResolutionException("No SelenideDrivers found in store");
        }

        return drivers.toArray(new SelenideDriver[0]);
    }
}
