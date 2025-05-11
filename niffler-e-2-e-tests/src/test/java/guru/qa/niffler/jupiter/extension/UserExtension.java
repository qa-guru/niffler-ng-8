package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.db.service.UserClient;
import guru.qa.niffler.db.service.impl.UserDbClient;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Map;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private final UserClient userClient = new UserDbClient();
    public static final String DEFAULT_PASSWORD = "123";
    public static final Map<String, String> USERS = Map.of("user", "user");

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    String username = userAnno.username();
                    UserParts user;
                    if ("".equals(username)) {
                        user = genDefaultUser(DEFAULT_PASSWORD);
                        user = userClient.createUser(user);
                        user.setPassword(DEFAULT_PASSWORD);
                    } else {
                        String password = USERS.get(username);
                        if (password != null) {
                            user = UserParts.of(username, password);
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                    extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), User.class)
            && parameterContext.getParameter().getType() == UserParts.class;
    }

    @Override
    public UserParts resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    public static UserParts createdUser() {
        ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserParts.class);
    }

}
