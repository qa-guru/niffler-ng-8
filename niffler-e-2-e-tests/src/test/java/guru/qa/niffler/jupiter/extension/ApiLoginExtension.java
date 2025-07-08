package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.TradeSafeCookieStore;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.service.impl.api.AuthApiClient;
import guru.qa.niffler.service.impl.api.SpendApiClient;
import guru.qa.niffler.service.impl.api.UserApiClient;
import guru.qa.niffler.util.TestData;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Parameter;
import java.util.List;

import static guru.qa.niffler.api.core.TradeSafeCookieStore.JSESSIONID;
import static guru.qa.niffler.jupiter.extension.UserExtension.getCreatedUser;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private static final String TOKEN_KEY = "TOKEN";
    private static final String CODE_KEY = "CODE";
    private static final AuthApiClient authClient = AuthApiClient.client();
    private static final SpendApiClient spendClient = SpendApiClient.client();
    private static final UserApiClient userClient = UserApiClient.client();
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
                UserParts user = resolveUser(annotation);
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

    private UserParts resolveUser(ApiLogin annotation) {
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
            fillTestData(user);
            UserExtension.setUser(user);
        }
        return user;
    }

    private void fillTestData(UserParts user) {
        fillSpendsAndCategories(user);
        fillFriendship(user);
    }

    private void fillFriendship(UserParts user) {
        TestData testData = user.getTestData();
        List<UserdataUserJson> friends = userClient.getAllFriends(user.getUsername());
        for (UserdataUserJson friend : friends) {
            switch (friend.getFriendshipStatus()) {
                case "FRIEND" -> testData.getFriendsNames().add(friend.getUsername());
                case "INVITE_SENT" -> testData.getOutInviteNames().add(friend.getUsername());
                case "INVITE_RECEIVED" -> testData.getInInviteNames().add(friend.getUsername());
            }
        }
    }

    private void fillSpendsAndCategories(UserParts user) {
        String username = user.getUsername();
        TestData testData = user.getTestData();
        List<SpendJson> allSpends = spendClient.getAllSpends(username);
        if (!CollectionUtils.isEmpty(allSpends)) {
            testData.getSpends().addAll(allSpends);
            List<CategoryJson> categories = allSpends.stream().map(SpendJson::category).toList();
            testData.getCategories().addAll(categories);
        } else {
            List<CategoryJson> categories = spendClient.getAllCategories(username);
            testData.getCategories().addAll(categories);
        }
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
