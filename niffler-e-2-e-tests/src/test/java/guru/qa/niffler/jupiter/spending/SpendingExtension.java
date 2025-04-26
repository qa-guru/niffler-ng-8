package guru.qa.niffler.jupiter.spending;

import com.github.javafaker.Faker;
import guru.qa.niffler.RandomDataUtils;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.users.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;
import java.util.UUID;

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        SpendDbClient db = new SpendDbClient();
        UUID id = RandomDataUtils.generateID();
        String categoryName = new Faker().company().name();
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if (anno.spendings() == null || anno.spendings().length == 0) {
                        return;
                    }
                    SpendJson spendJson = new SpendJson(
                            id,
                            new Date(),
                            new CategoryJson(
                                    null,
                                    categoryName,
                                    anno.username(),
                                    false
                            ),
                            anno.spendings()[0].currency(),
                            anno.spendings()[0].amount(),
                            anno.spendings()[0].description(),
                            anno.username()
                    );

                    SpendJson created = db.createSpend(spendJson);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        SpendJson spend = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendJson.class);
        SpendDbClient db = new SpendDbClient();
        if (spend != null) {
            db.deleteSpend(spend);
        }
        if (category != null) {
            db.deleteCategory(category);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
