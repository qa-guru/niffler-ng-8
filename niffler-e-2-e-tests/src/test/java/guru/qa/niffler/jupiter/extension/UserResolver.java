package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.db.service.impl.UserDbClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.jupiter.annotation.User.Mode.GEN;
import static guru.qa.niffler.util.RandomDataUtils.*;

public class UserResolver implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserResolver.class);
    private static final UserDbClient USER_DB_CLIENT = new UserDbClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    WebUser user = resolve(userAnno);
                    extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), User.class)
                && parameterContext.getParameter().getType() == WebUser.class;
    }

    @Override
    public WebUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), WebUser.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.value() == GEN) {
                        WebUser user = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), WebUser.class);
                        if (user != null) {
                            USER_DB_CLIENT.deleteUser(UserParts.of(user.username()));
                        }
                    }
                });
    }

    public static WebUser resolve(User useUser) {
        return switch (useUser.value()) {
            case GEN -> {
                WebUser user = new WebUser(genUsername(), genPassword());
                createUser(user);
                yield user;
            }
            case DEFAULT -> new WebUser("user", "user");
        };
    }

    private static void createUser(WebUser webUser) {
        UserParts user = genDefaultUser(webUser.username(), webUser.password());
        USER_DB_CLIENT.createUser(user);
    }

}
