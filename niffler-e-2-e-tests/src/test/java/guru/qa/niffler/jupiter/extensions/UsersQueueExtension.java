package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.config.Config;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String userName, String pass,
            String friend,
            String income,
            String outcome) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_FRIEND = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_FRIEND = new ConcurrentLinkedDeque<>();

    static {
        EMPTY_USERS.add(new StaticUser("emptyOne", "pass1", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("withFriendOne", "pass2", CFG.mainUserLogin(), null, null));
        WITH_INCOME_REQUEST_FRIEND.add(new StaticUser("withIncomeFOne", "pass3", null, "withOutcomeFOne", null));
        WITH_OUTCOME_REQUEST_FRIEND.add(new StaticUser("withOutcomeFOne", "pass4", null, null, "withIncomeFOne"));

    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
                .filter(param -> param.getType().equals(StaticUser.class))
                .map(param -> param.getAnnotation(UserType.class))
                .forEach(userType -> {
                    StopWatch stopWatch = StopWatch.createStarted();
                    StaticUser user = null;
                    while (user == null && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                        user = getQueue(userType).poll();
                    }
                    Optional.ofNullable(user).ifPresentOrElse(
                            u -> {
                                Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                        .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());
                                userMap.put(userType, u);
                            },
                            () -> {
                                throw new IllegalStateException("Юзер не был найден за 30 секунд");
                            });
                });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        //Вернули обратно в очередь
        Map<UserType, StaticUser> users = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        if (users != null) {
            users.forEach((key, value) -> getQueue(key).add(value));
        }
    }

    private Queue<StaticUser> getQueue(UserType userType) {
        return switch (userType.value()) {
            case EMPTY -> EMPTY_USERS;
            case WITH_FRIEND -> WITH_FRIEND_USERS;
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_FRIEND;
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_FRIEND;
        };
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        UserType userType = AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class)
                .orElseThrow(() -> new ParameterResolutionException("UserType annotation is missing"));
        return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class).get(userType);
    }
}
