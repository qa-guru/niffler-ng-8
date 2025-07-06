package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.TradeSafeCookieStore;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.service.impl.api.AuthApiClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.lang.reflect.Parameter;

import static guru.qa.niffler.api.core.TradeSafeCookieStore.JSESSIONID;
import static guru.qa.niffler.jupiter.extension.UserExtension.getCreatedUser;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private static final String TOKEN_KEY = "TOKEN";
    private static final String CODE_KEY = "CODE";
    private static final AuthApiClient authClient = AuthApiClient.client();
    private static final Config CFG = Config.getInstance();
    private final boolean setupBrother;

    public ApiLoginExtension(boolean setupBrother) {
        this.setupBrother = setupBrother;
    }

    public ApiLoginExtension() {
        this.setupBrother = true;
    }

    public static ApiLoginExtension restApiLOGinExtension() {
        return new ApiLoginExtension(false);
    }

    @Override

    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
            .ifPresent(annotation -> {
                UserParts user;
                UserParts userFromUserExtension = getCreatedUser();
                if (StringUtils.isBlank(annotation.username()) || StringUtils.isBlank(annotation.password())) {
                    if (userFromUserExtension == null) {
                        throw new IllegalStateException("@User must be present in case then @ApiLogin is empty!");
                    }
                    user = userFromUserExtension;
                } else {
                    user = UserParts.of(annotation.username(), annotation.password());
                    if (userFromUserExtension != null) {
                        throw new IllegalStateException("@User must not be present in case then @ApiLogin contains username or password!");
                    }
                    UserExtension.setUser(user);
                }
                String token = authClient.successLoginAdnGetToken(user);
                setToken(token);
                if (setupBrother) {
                    Selenide.open(CFG.frontUrl());
                    Selenide.localStorage().setItem("id_token", getToken());
                    WebDriverRunner.getWebDriver().manage().addCookie(
                        getJSessionIdCookie()
                    );
                }
            });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getType().isAssignableFrom(String.class)
            && AnnotationSupport.isAnnotated(parameter, Token.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put(TOKEN_KEY, token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get(TOKEN_KEY, String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put(CODE_KEY, code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get(CODE_KEY, String.class);
    }

    public static Cookie getJSessionIdCookie() {
        return new Cookie(JSESSIONID, TradeSafeCookieStore.INSTANCE.jSessionIdValue());
    }
}
