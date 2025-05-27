package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

@ParametersAreNonnullByDefault
public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.categories())) {
            final UserJson user = UserExtension.createdUser();

            final String username = user != null
                ? user.username()
                : userAnno.username();

            final List<CategoryJson> createdCategories = new ArrayList<>();

            for (Category categoryAnno : userAnno.categories()) {
              CategoryJson category = new CategoryJson(
                  null,
                  "".equals(categoryAnno.name()) ? randomCategoryName() : categoryAnno.name(),
                  username,
                  categoryAnno.archived()
              );
              createdCategories.add(
                  spendClient.createCategory(category)
              );
            }
            if (user != null) {
              user.testData().categories().addAll(
                  createdCategories
              );
            } else {
              context.getStore(NAMESPACE).put(
                  context.getUniqueId(),
                  createdCategories
              );
            }
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }

  @Override
  public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdCategories().toArray(CategoryJson[]::new);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static List<CategoryJson> createdCategories() {
    final ExtensionContext context = TestsMethodContextExtension.context();
    return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
        .orElse(Collections.emptyList());
  }
}
