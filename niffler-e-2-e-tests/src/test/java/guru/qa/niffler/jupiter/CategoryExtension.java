package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();
  Faker faker = new Faker();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
      .ifPresent(annotation -> {
        CategoryJson categoryJson = new CategoryJson(
          null,
          faker.commerce().department(),
          annotation.username(),
          false
        );
        CategoryJson createdCategory = spendApiClient.addCategory(categoryJson);
        if (annotation.archived()) {
          CategoryJson archivedCategory = new CategoryJson(
            createdCategory.id(),
            createdCategory.name(),
            createdCategory.username(),
            true
          );
          createdCategory = spendApiClient.updateCategory(archivedCategory);
        }
        context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
      });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    CategoryJson category = context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
    if (!category.archived()) {
      CategoryJson archivedCategory = new CategoryJson(
        category.id(),
        category.name(),
        category.username(),
        true
      );
      spendApiClient.updateCategory(archivedCategory);
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