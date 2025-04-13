package guru.qa.niffler.jupiter.extention;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnnotation -> {
                    if (ArrayUtils.isNotEmpty(userAnnotation.spendings())) {
                        SpendJson spendJson = new SpendJson(
                                null,
                                new Date(),
                                new CategoryJson(
                                        null,
                                        userAnnotation.spendings()[0].category(),
                                        userAnnotation.username(),
                                        false
                                ),
                                userAnnotation.spendings()[0].currency(),
                                userAnnotation.spendings()[0].amount(),
                                userAnnotation.spendings()[0].description(),
                                userAnnotation.username()
                        );

                        SpendJson created = spendApiClient.addSpend(spendJson);
                        context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
