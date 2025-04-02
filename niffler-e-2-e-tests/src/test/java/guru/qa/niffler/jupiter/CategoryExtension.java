package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private Faker faker = new Faker();


    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    String categoryName = faker.commerce().department();
                    CategoryJson category = new CategoryJson(null, categoryName, anno.username(), false);
                    CategoryJson created = spendApiClient.createCategory(category);
                    if (anno.archived()) {
                        CategoryJson archivedCategory = new CategoryJson(
                                created.id(),
                                created.name(),
                                created.username(),
                                true
                        );
                        created = spendApiClient.updateCategory(archivedCategory);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson category = context.getStore(CreateSpendingExtension.NAMESPACE)
                .get(context.getUniqueId(), CategoryJson.class);
        if (category != null) {
            if(!category.archived()) {
                CategoryJson categoryArchived = new CategoryJson(category.id(), category.name(), category.username(), true);
                spendApiClient.updateCategory(categoryArchived);
            }
        }
    }
}
