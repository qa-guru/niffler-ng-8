package guru.qa.niffler.jupiter.category;

import guru.qa.niffler.InputGenerator;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.spending.CreateSpendingExtension;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String name = InputGenerator.getRandomString(5);
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJson =
                            new CategoryJson(
                                    null,
                                    name,
                                    anno.username(),
                                    false
                            );

                    CategoryJson created = spendApiClient.addSpendCategories(categoryJson);
                    if (anno.archived()) {
                        CategoryJson archivedCategory = new CategoryJson(
                                created.id(),
                                name,
                                created.username(),
                                true
                        );
                        created = spendApiClient.updateCategories(archivedCategory);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(),
                CategoryJson.class);
        if (!category.archived()) {
            AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                    .ifPresent(anno -> {
                        CategoryJson categoryJson =
                                new CategoryJson(
                                        category.id(),
                                        category.name(),
                                        category.username(),
                                        true
                                );
                        spendApiClient.updateCategories(categoryJson);
                    });
        }
    }
}


