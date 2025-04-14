package guru.qa.niffler.jupiter.category;

import guru.qa.niffler.RandomDataUtils;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.spending.SpendingExtension;
import guru.qa.niffler.jupiter.users.User;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(user -> {
                    if (user.categories() == null || user.categories().length == 0) {
                        return;
                    }
                    Category category = user.categories()[0];
                    CategoryJson categoryJson = spendApiClient.addSpendCategories(new CategoryJson(
                            null,
                            RandomDataUtils.categoryName(),
                            user.username(),
                            false
                    ));
                    if (category.archived()) {
                        categoryJson = spendApiClient.updateCategories(new CategoryJson(
                                categoryJson.id(),
                                categoryJson.name(),
                                categoryJson.username(),
                                true
                        ));
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), categoryJson);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (category != null && !category.archived()) {
            spendApiClient.updateCategories(new CategoryJson(
                    category.id(),
                    category.name(),
                    category.username(),
                    true
            ));
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


