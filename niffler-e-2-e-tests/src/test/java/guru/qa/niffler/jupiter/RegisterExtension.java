package guru.qa.niffler.jupiter;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.User;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class RegisterExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(),DoRegister.class)
                .ifPresent(anno ->{
                    Faker faker = new Faker();
                    User user = new User(
                            faker.internet().emailAddress(),
                            faker.internet().password(3,12)
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
