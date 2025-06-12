package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.db.SpendDbClient;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class CreateSpendingExtension implements BeforeEachCallback {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
  private final SpendDbClient spendDbClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(annotation -> {
          Spending[] spendings = annotation.spendings();
          if (spendings.length > 0) {
            Spending spending = spendings[0];
            String categoryName = String.format("%s %s", spending.category(), RandomDataUtils.randomCategoryName());
            SpendJson spendJson = new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                    null,
                    categoryName,
                    annotation.username(),
                    false
                ),
                spending.currency(),
                spending.amount(),
                spending.description(),
                annotation.username()
            );
            spendDbClient.create(spendJson);
            context.getStore(NAMESPACE).put(context.getUniqueId(), spendJson);
          }
        });
    }
}
