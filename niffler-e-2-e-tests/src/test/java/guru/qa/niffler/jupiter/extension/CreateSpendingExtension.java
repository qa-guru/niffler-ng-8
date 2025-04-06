package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendService;
import guru.qa.niffler.api.SpendServiceClient;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.retrofit.TestResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class CreateSpendingExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
    private final SpendServiceClient spendServiceApiClient = SpendService.client();

    @Override
    @SneakyThrows
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
                    TestResponse<SpendJson, Void> response = spendServiceApiClient.addSpend(spendJson);
                    if (response.isSuccessful()) {
                        context.getStore(NAMESPACE).put(context.getUniqueId(), response.getBody());
                    } else {
                        Assertions.fail("Ошибка при создании трат. Ответ:\n" + response.getRetrofitRawResponse());
                    }
                });
    }
}
