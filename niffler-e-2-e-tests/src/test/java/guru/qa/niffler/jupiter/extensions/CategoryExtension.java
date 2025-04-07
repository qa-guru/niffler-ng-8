package guru.qa.niffler.jupiter.extensions;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final Faker faker = new Faker();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
            .ifPresent(annotation -> {
                String username = faker.name().username();
                CategoryJson category = new CategoryJson(
                        null,
                        username,
                        annotation.username(),
                        false
                );
                category = spendApiClient.addCategory(category);
                if (annotation.archived()) {
                    category = new CategoryJson(
                            category.id(),
                            username,
                            annotation.username(),
                            true
                    );
                    spendApiClient.updateCategory(category);
                }
                context.getStore(NAMESPACE).put(context.getUniqueId(), category);
        });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);

        if (category != null && category.archived()) {
            category = new CategoryJson(
                    category.id(),
                    category.name(),
                    category.username(),
                    true
            );

            spendApiClient.updateCategory(category);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return context.getRequiredTestMethod().isAnnotationPresent(Category.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
    }
}
