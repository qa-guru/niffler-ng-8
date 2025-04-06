package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DoRegister;
import guru.qa.niffler.model.User;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegistrationPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class RegisterExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(RegisterExtension.class);
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), DoRegister.class)
                .ifPresent(anno ->{
                    User user = new User(
                            RandomDataUtils.randomEmail(),
                            RandomDataUtils.randomPassword(3,12)
                    );
                    Selenide.open(Config.getInstance().frontUrl(), LoginPage.class)
                            .clickRegister()
                            .assertRedirectToPage(RegistrationPage.class)
                            .setUserName(user.username())
                            .setPassword(user.password())
                            .setPasswordSubmit(user.password())
                            .clickSignUp()
                            .clickSignIn()
                            .assertRedirectToPage(LoginPage.class);
                    Selenide.closeWebDriver();
                    context.getStore(NAMESPACE).put(context.getUniqueId(), user);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(User.class);
    }

    @Override
    public User resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), User.class);
    }
}
