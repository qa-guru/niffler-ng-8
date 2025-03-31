package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback,ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private static final String BEFORE_EACH_CALLBACK_KEY = "_beforeEachCallback";
    private static final String AFTER_EACH_CALLBACK_KEY = "_afterEachCallback";

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJson = new CategoryJson(
                            null,
                            new Faker().name().name(),
                            anno.username(),
                            false
                    );
                    CategoryJson created = spendApiClient.addCategory(categoryJson);
                    if (anno.archived()) {
                        created = archiveCategory(created);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId() + BEFORE_EACH_CALLBACK_KEY, created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(
                        anno -> {
                            CategoryJson categoryJson = context.getStore(NAMESPACE)
                                    .get(context.getUniqueId() + BEFORE_EACH_CALLBACK_KEY, CategoryJson.class);
                            if (!categoryJson.archived()) {
                                CategoryJson updatedCategoryJson = archiveCategory(categoryJson);
                                context.getStore(NAMESPACE).put(context.getUniqueId() + AFTER_EACH_CALLBACK_KEY, updatedCategoryJson);
                            }
                        });

    }

    private CategoryJson archiveCategory(CategoryJson categoryJson) {
        CategoryJson archivedCategory = new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                true
        );
        return spendApiClient.updateCategory(archivedCategory);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId()+BEFORE_EACH_CALLBACK_KEY, CategoryJson.class);
    }
}


