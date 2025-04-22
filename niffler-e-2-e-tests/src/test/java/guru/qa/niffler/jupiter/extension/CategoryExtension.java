package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import guru.qa.niffler.Utils.RandomDataUtils;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private RandomDataUtils dataUtils = new RandomDataUtils();


    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if (ArrayUtils.isNotEmpty(anno.categories())) {
                        String categoryName = dataUtils.randomCategoryName();
                        CategoryJson category = new CategoryJson(null, categoryName, anno.username(), false);
                        CategoryJson created = spendApiClient.createCategory(category);
                        if (anno.categories()[0].archived()) {
                            CategoryJson archivedCategory = new CategoryJson(
                                    created.id(),
                                    created.name(),
                                    created.username(),
                                    true
                            );
                            created = spendApiClient.updateCategory(archivedCategory);
                        }
                        context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                    }

                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), CategoryJson.class);
        if (category != null) {
            if(!category.archived()) {
                CategoryJson categoryArchived = new CategoryJson(category.id(), category.name(), category.username(), true);
                spendApiClient.updateCategory(categoryArchived);
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
