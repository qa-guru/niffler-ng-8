package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback,ParameterResolver {
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final Faker faker = new Faker();

    @Override
    public void beforeEach(ExtensionContext context) {
        Category annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .orElse(null);
        final String username = faker.name().username();

        if (annotation != null) {
            CategoryJson category = new CategoryJson(
                    null,
                    username,
                    annotation.username(),
                    annotation.archived()
            );

            spendApiClient.addCategory(category);

            if (annotation.archived()) {
                CategoryJson archCategoryJson = new CategoryJson(
                        category.id(),
                        username,
                        annotation.username(),
                        true
                );
                spendApiClient.updateCategory(archCategoryJson);
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Category annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .orElse(null);
        final String username = faker.name().username();

        if (annotation != null && !annotation.archived()) {
            CategoryJson category = new CategoryJson(
                    null,
                    username,
                    annotation.username(),
                    true
            );
            spendApiClient.updateCategory(category);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }
}
