package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import java.util.Date;
import java.util.Optional;

public class CreateSpendingExtension implements BeforeEachCallback {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(annotation -> {
          Optional<Spending> spending = Optional.ofNullable(annotation.spendings()[0]);
          spending.ifPresent(sp -> {
              SpendJson spendJson = new SpendJson(
                  null,
                  new Date(),
                  new CategoryJson(
                          null,
                          sp.category(),
                          annotation.username(),
                          false
                  ),
                  sp.currency(),
                  sp.amount(),
                  sp.description(),
                  annotation.username()
              );

              SpendJson created = spendApiClient.addSpend(spendJson);
              context.getStore(NAMESPACE).put(context.getUniqueId(), created);
          });
        });
  }
}
