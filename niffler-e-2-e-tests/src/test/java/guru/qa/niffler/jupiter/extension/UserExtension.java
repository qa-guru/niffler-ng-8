package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;

public class UserExtension implements
        ParameterResolver,
        BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private final UsersClient userClient = new UsersApiClient();
    private static final String defaultPassword = "12345";

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    final String resolvedUsername =
                            (userAnno.username() == null || userAnno.username().trim().isEmpty())
                                    ? RandomDataUtils.randomUsername()
                                    : userAnno.username();

                    UserJson user = userClient.findUserByUsername(resolvedUsername)
                            .orElseGet(() -> userClient.createUser(resolvedUsername, defaultPassword));

                    if (userAnno.incomeInvitations() > 0) {
                        userClient.createIncomeInvitations(user, userAnno.incomeInvitations());
                    }
                    if (userAnno.outcomeInvitations() > 0) {
                        userClient.createOutcomeInvitations(user, userAnno.outcomeInvitations());
                    }
                    if (userAnno.friends() > 0) {
                        userClient.createFriends(user, userAnno.friends());
                    }

                    context.getStore(NAMESPACE).put(context.getUniqueId(), user.withPassword(defaultPassword));
                });
    }



    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return contextUser(extensionContext);
    }

    public static @Nullable UserJson contextUser(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }
}