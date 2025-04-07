package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.jupiter.annotations.UserType;
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
            String username,
            String password,
            String friend,
            String income,
            String outcome) {}

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("userEmpty", "1234567", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("lesnikov", "12345", "ilesnikov", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("ilya", "12345", null, "ilyalesnikov", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("ilyalesnikov", "123456", null, null, "ilya"));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> AnnotationSupport.isAnnotated(parameter, UserType.class))
                .map(parameter -> parameter.getAnnotation(UserType.class))
                .forEach(userType -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch stopWatch = StopWatch.createStarted();
                    while (user.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                        user = userQueuePoll(userType.type());
                    }
                    Allure.getLifecycle().updateTestCase(testCase -> {
                        testCase.setStart(new Date().getTime());
                    });
                    user.ifPresentOrElse(
                            u -> ((Map<UserType, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                                    context.getUniqueId(),
                                    key -> new HashMap()
                            )).put(userType, u),
                            () -> { throw new IllegalStateException("Can`t find user after 30 sec"); }
                    );
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (Map.Entry<UserType, StaticUser> e : map.entrySet()) {
            switch (e.getKey().type()) {
                case EMPTY -> EMPTY_USERS.add(e.getValue());
                case WITH_FRIEND -> WITH_FRIEND_USERS.add(e.getValue());
                case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(e.getValue());
                case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(e.getValue());
            }
        }

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return (StaticUser) context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class)
                .get(AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).get());
    }

    private Optional<StaticUser> userQueuePoll(UserType.Type userType) {
        return switch (userType) {
            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
            case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
            case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
        };
    }
}
