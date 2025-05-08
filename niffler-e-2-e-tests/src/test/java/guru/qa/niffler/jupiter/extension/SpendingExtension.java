package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;


import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.db.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;

import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                        UserJson createdUser = UserExtension.createdUser();
                        final String username = createdUser != null
                                ? createdUser.username()
                                : userAnno.username();

                        final List<SpendJson> createdSpendings = new ArrayList<>();

                        for (Spending spendAnno : userAnno.spendings()) {
                            String categoryName = spendAnno.category().equals("")
                                    ? RandomDataUtils.randomCategoryName()
                                    : spendAnno.category();

                            double amount = spendAnno.amount()==-1.0
                                    ? RandomDataUtils.randomSpendingAmount()
                                    : spendAnno.amount();

                            String description = spendAnno.description().equals("")
                                    ? RandomDataUtils.randomSpendingDescription()
                                    :spendAnno.description();

                            UUID categoryId = createdUser.testData().categories()
                                    .stream()
                                    .filter(c->c.name().equals(categoryName)
                                    && c.username().equals(username))
                                    .findFirst()
                                    .map(CategoryJson::id)
                                    .orElse(null);

                            boolean archived = createdUser.testData().categories()
                                    .stream()
                                    .filter(c->c.name().equals(categoryName)
                                            && c.username().equals(username))
                                    .findFirst()
                                    .map(CategoryJson::archived)
                                    .orElse(false);

                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    new CategoryJson(
                                            categoryId,
                                            categoryName,
                                            username,
                                            archived
                                    ),
                                    spendAnno.currency(),
                                    amount,
                                    description,
                                    username
                            );
                            spend = spendClient.createSpend(spend);
                            createdSpendings.add(spend);
                            if(createdUser
                                    .testData()
                                    .categories()
                                    .stream()
                                    .noneMatch(c->c.name().equals(categoryName))){
                                createdUser.testData().categories().add(spend.category());
                            }
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
        return (SpendJson[]) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class)
                .stream()
                .toArray(SpendJson[]::new);
    }
}
