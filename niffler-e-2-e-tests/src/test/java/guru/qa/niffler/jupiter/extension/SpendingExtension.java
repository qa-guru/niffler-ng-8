package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
            UserJson createdUser = UserExtension.createdUser();
            final String username = createdUser != null
                ? createdUser.username()
                : userAnno.username();

            final List<SpendJson> createdSpendings = new ArrayList<>();

            for (Spend spendAnno : userAnno.spendings()) {
              SpendJson spend = new SpendJson(
                  null,
                  new Date(),
                  new CategoryJson(
                      null,
                      spendAnno.category(),
                      username,
                      false
                  ),
                  CurrencyValues.RUB,
                  spendAnno.amount(),
                  spendAnno.description(),
                  username
              );
              createdSpendings.add(
                  spendClient.createSpend(spend)
              );
            }

            if (createdUser != null) {
              createdUser.testData().spendings().addAll(
                  createdSpendings
              );
            } else {
              context.getStore(NAMESPACE).put(
                  context.getUniqueId(),
                  createdSpendings
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
  @SuppressWarnings("unchecked")
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (SpendJson[]) extensionContext.getStore(SpendingExtension.NAMESPACE)
        .get(extensionContext.getUniqueId(), List.class)
        .stream()
        .toArray(SpendJson[]::new);
  }
}
