package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.db.UserDbClient;
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
                UserParts user = getUser(username);
                addFriends(userAnno, user);
                setUser(user);
            });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == UserParts.class;
    }

    @Override
    public UserParts resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getCreatedUser();
    }

    public static UserParts getCreatedUser() {
        ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserParts.class);
    }

    public static void setUser(UserParts user) {
        ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(context.getUniqueId(), user);
    }

    private void addFriends(User userAnno, UserParts user) {
        if (userAnno.withFriend() > 0) {
            userClient.createFriends(user, userAnno.withFriend());
        }
        if (userAnno.withInInvite() > 0) {
            userClient.createIncomeInvitation(user, userAnno.withInInvite());
        }
        if (userAnno.withOutInvite() > 0) {
            userClient.createOutcomeInvitation(user, userAnno.withOutInvite());
        }
    }

    private UserParts getUser(String username) {
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
        return user;
    }

}
