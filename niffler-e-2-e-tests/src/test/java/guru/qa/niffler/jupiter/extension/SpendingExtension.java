package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
            final UserJson user = UserExtension.createdUser();
            final String username = user != null
                ? user.username()
                : userAnno.username();

            final List<CategoryJson> existingCategories = user != null
                ? user.testData().categories()
                : CategoryExtension.createdCategories();

            final List<SpendJson> createdSpends = new ArrayList<>();

            for (Spend spendAnno : userAnno.spendings()) {
              final Optional<CategoryJson> matchedCategory = existingCategories.stream()
                  .filter(cat -> cat.name().equals(spendAnno.category()))
                  .findFirst();

              SpendJson spend = new SpendJson(
                  null,
                  new Date(),
                  matchedCategory.orElseGet(() -> new CategoryJson(
                      null,
                      spendAnno.category(),
                      username,
                      false
                  )),
                  spendAnno.currency(),
                  spendAnno.amount(),
                  spendAnno.description(),
                  username
              );

              createdSpends.add(
                  spendClient.createSpend(spend)
              );
            }
            if (user != null) {
              user.testData().spendings().addAll(
                  createdSpends
              );
            } else {
              context.getStore(NAMESPACE).put(
                  context.getUniqueId(),
                  createdSpends
              );
            }
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdSpends(extensionContext).toArray(SpendJson[]::new);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static List<SpendJson> createdSpends(ExtensionContext extensionContext) {
    return Optional.ofNullable(extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class))
        .orElse(Collections.emptyList());
  }
}
