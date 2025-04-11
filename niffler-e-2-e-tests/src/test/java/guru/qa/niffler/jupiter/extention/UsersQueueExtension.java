package guru.qa.niffler.jupiter.extention;

import guru.qa.niffler.jupiter.annotation.UserType;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String userName,
            String password,
            String friend,
            String income,
            String outcome) {

    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();


    static {
        EMPTY_USERS.add(new StaticUser("test0401", "test0401", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("test0402", "test0402", "test0403", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("test0403", "test0403", null, "test0401", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("test0404", "test0404", null, null, "test0405"));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> AnnotationSupport.isAnnotated(parameter, UserType.class) && parameter.getType().isAssignableFrom(StaticUser.class))
                .map(parameter -> parameter.getAnnotation(UserType.class))
                .forEach(type -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch stopWatch = StopWatch.createStarted();

                    while (user.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                        user = setUserType(type);

                        Allure.getLifecycle().updateTestCase(tc ->
                                tc.setStart(new Date().getTime()));

                    }
                    user.ifPresentOrElse(
                            u ->
                                    ((Map<UserType, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                                            context.getUniqueId(),
                                            key -> new HashMap<>()
                                    )).put(type, u),
                            () -> {
                                throw new IllegalStateException("Can`t obtain user after 30s");
                            }
                    );
                });

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Map<UserType, StaticUser> userMap = context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                Map.class
        );
        if (userMap != null) {
            for (Map.Entry<UserType, StaticUser> e : userMap.entrySet()) {
               setUserType(e.getKey());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        System.err.println(extensionContext.getUniqueId());
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
                .get(AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).orElseThrow());
    }

    private Optional<StaticUser> setUserType(guru.qa.niffler.jupiter.annotation.UserType type) {
        return switch (type.value()) {
            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
            case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
            case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
        };
    }
}
