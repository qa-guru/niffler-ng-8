package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.service.impl.db.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        UserParts user = UserExtension.createdUser();
        List<Spending> spendings = getSpendings(extensionContext);
        if (!spendings.isEmpty()) {
            List<SpendJson> createdSpendings = new ArrayList<>();

            for (Spending spend : spendings) {
                SpendJson spendJson = new SpendJson(
                    null,
                    new Date(),
                    new CategoryJson(
                        null,
                        spend.category(),
                        user.getUsername(),
                        false
                    ),
                    spend.currency(),
                    spend.amount(),
                    spend.description(),
                    user.getUsername()
                );
                SpendJson createdSpend = spendDbClient.createSpend(spendJson);
                createdSpendings.add(createdSpend);
                user.getTestData().getSpends().add(createdSpend);
            }
            extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdSpendings);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        Type parameterizedType = parameter.getParameterizedType();
        boolean isListOfSpend =
            type == List.class
                && parameterizedType instanceof ParameterizedType
                && ((ParameterizedType) parameterizedType).getActualTypeArguments()[0] == SpendJson.class;
        return !getSpendings(extensionContext).isEmpty() && (type == SpendJson.class || isListOfSpend);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        List<SpendJson> createdSpendings = extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), List.class);
        Class<?> paramType = parameterContext.getParameter().getType();
        if (paramType == SpendJson.class) {
            return createdSpendings.get(0);
        } else if (paramType == List.class) {
            return createdSpendings;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        List<SpendJson> createdSpendings = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class);
        if (createdSpendings != null) {
            createdSpendings.forEach(spendDbClient::deleteSpend);
        }
    }

    private List<Spending> getSpendings(ExtensionContext extensionContext) {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
            .filter(user -> user.spendings().length > 0)
            .map(user -> Arrays.asList(user.spendings()))
            .orElse(Collections.emptyList());
    }

}
