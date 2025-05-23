package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        SpendDbClient db = new SpendDbClient();
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(user -> {
                    if (user.categories() == null || user.categories().length == 0) {
                        return;
                    }
                    CategoryJson categoryJson = db.createTxCategory(new CategoryJson(
                            null,
                            RandomDataUtils.categoryName(),
                            user.username(),
                            user.categories()[0].archived()

                    ));
                    context.getStore(NAMESPACE).put(context.getUniqueId(), categoryJson);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        SpendDbClient db = new SpendDbClient();
        if (category != null) {
            db.deleteTxCategory(category);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}


