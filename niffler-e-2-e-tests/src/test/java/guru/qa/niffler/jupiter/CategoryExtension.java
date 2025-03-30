package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();
  private final Faker faker = new Faker();
  private CategoryJson created;

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
        .ifPresent(anno -> {
          CategoryJson category = new CategoryJson(
              null,
              faker.commerce().productName(),
              anno.username(),
              anno.archived()
          );

          created = spendApiClient.createCategory(category);
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
  public void afterTestExecution(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
        .ifPresent(anno -> {
          if (anno.archived()) {
            CategoryJson archivedCategory = new CategoryJson(
                created.id(),
                created.name(),
                created.username(),
                true
            );
            spendApiClient.updateCategory(archivedCategory);
          }
        });
  }
}
