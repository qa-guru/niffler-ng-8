package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UseUser;
import guru.qa.niffler.web.model.User;
import guru.qa.niffler.web.page.LoginPage;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.jupiter.annotation.UseUser.Mode.DEFAULT;
import static guru.qa.niffler.jupiter.annotation.UseUser.Mode.GEN;
import static guru.qa.niffler.util.GenerationUtil.genPassword;
import static guru.qa.niffler.util.GenerationUtil.genUsername;

public class UseUserResolver implements ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UseUserResolver.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), UseUser.class)
                && parameterContext.getParameter().getType() == User.class;
    }

    @Override
    public User resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        UseUser useUser = AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), UseUser.class).get();
        User user = resolve(useUser);
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);
        return user;
    }

    public static User resolve(UseUser useUser) {
        User user = null;
        if (useUser.value() == GEN) {
            user = new User(genUsername(), genPassword());
            createUser(user);
        }
        if (useUser.value() == DEFAULT) {
            user = new User("user", "user");
        }
        return user;
    }

    private static void createUser(User user) {
        Selenide.open(Config.getInstance().frontUrl(), LoginPage.class)
                .clickCreateNewUserBtn()
                .registerUserSuccess(user.username(), user.password(), user.password())
                .checkSuccessfulRegistrationPage();
    }

}
