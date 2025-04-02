package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class CreateSpendingExtension implements BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spend.class)
        .ifPresent(anno -> {
          SpendJson spendJson = new SpendJson(
              null,
              new Date(),
              new CategoryJson(
                  null,
                  anno.category(),
                  anno.username(),
                  false
              ),
              anno.currency(),
              anno.amount(),
              anno.description(),
              anno.username()
          );

          SpendJson created = spendApiClient.createSpend(spendJson);
          context.getStore(NAMESPACE).put(context.getUniqueId(), created);
        });
  }
}
