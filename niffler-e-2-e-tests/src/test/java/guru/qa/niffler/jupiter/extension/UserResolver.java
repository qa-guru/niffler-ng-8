package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import guru.qa.niffler.web.page.LoginPage;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.util.RandomDataUtils.genPassword;
import static guru.qa.niffler.util.RandomDataUtils.genUsername;

public class UserResolver implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserResolver.class);

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

    private static void createUser(WebUser user) {
        Selenide.open(Config.getInstance().frontUrl(), LoginPage.class)
                .clickCreateNewUserBtn()
                .registerUserSuccess(user.username(), user.password(), user.password())
                .checkSuccessfulRegistrationPage();
    }

}
