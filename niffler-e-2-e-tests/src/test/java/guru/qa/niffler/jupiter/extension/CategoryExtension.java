package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;


public class CategoryExtension implements BeforeEachCallback, ParameterResolver,AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> anno.categories().length > 0)
                .ifPresent(anno -> {
                    CategoryJson categoryJson =
                            new CategoryJson(
                                    null,
                                    RandomDataUtils.randomCategoryName(),
                                    anno.username(),
                                    false
                            );

                    CategoryJson created = spendApiClient.addCategory(categoryJson);
                    if (anno.categories()[0].archived()) {
                        created = archiveCategory(created);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> anno.categories().length > 0)
                .ifPresent(anno -> {
                    CategoryJson categoryJson = context.getStore(NAMESPACE)
                            .get(context.getUniqueId()
                                    , CategoryJson.class);
                    if (isUnarchived(categoryJson)) {
                        archiveCategory(categoryJson);
                    }
                });
    }

    private CategoryJson archiveCategory(CategoryJson categoryJson){
        CategoryJson archivedCategory =  new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                true
        );
        return spendApiClient.updateCategory(archivedCategory);
    }

    private boolean isUnarchived(CategoryJson categoryJson){
        return spendApiClient.getCategories(categoryJson.username(),true)
                .stream()
               .anyMatch(c -> c.id().equals(categoryJson.id()));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
