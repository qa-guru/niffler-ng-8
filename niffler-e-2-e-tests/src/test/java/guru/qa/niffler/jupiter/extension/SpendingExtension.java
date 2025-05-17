package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.db.service.impl.SpendDbClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;
import java.util.Optional;

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        getSpending(extensionContext)
                .ifPresent(spendAnno -> {
                    String username = extensionContext
                            .getStore(UserResolver.NAMESPACE)
                            .get(extensionContext.getUniqueId(), WebUser.class)
                            .username();
                    SpendJson spendJson = new SpendJson(
                            null,
                            new Date(),
                            new CategoryJson(
                                    null,
                                    spendAnno.category(),
                                    username,
                                    false
                            ),
                            spendAnno.currency(),
                            spendAnno.amount(),
                            spendAnno.description(),
                            username
                    );
                    SpendJson createdSpend = spendDbClient.createSpend(spendJson);
                    extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdSpend);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getSpending(extensionContext).isPresent() && parameterContext.getParameter().getType() == SpendJson.class;
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        SpendJson spendJson = extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
        if (spendJson != null) {
            spendDbClient.deleteSpend(spendJson);
        }
    }

    private Optional<Spending> getSpending(ExtensionContext extensionContext) {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
                .filter(user -> user.spendings().length > 0)
                .map(user -> user.spendings()[0]);
    }

}
