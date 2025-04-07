package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.*;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);
    private static final Map<Type, Queue<StaticUser>> USER_POOLS = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    static {
        USER_POOLS.put(Type.EMPTY, new ConcurrentLinkedQueue<>(List.of(
                new StaticUser("withoutFriend1", "123", null, null, null)
        )));
        USER_POOLS.put(Type.WITH_FRIEND, new ConcurrentLinkedQueue<>(List.of(
                new StaticUser("withFriend1", "withFriend1", "friend1", null, null)
        )));
        USER_POOLS.put(Type.WITH_INCOME_REQUEST, new ConcurrentLinkedQueue<>(List.of(
                new StaticUser("withInFriend1", "123", null, "friend1", null)
        )));
        USER_POOLS.put(Type.WITH_OUTCOME_REQUEST, new ConcurrentLinkedQueue<>(List.of(
                new StaticUser("withOutFriend1", "123", null, null, "friend1")
        )));
    }

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome
    ) {
    }

    @ExtendWith(UsersQueueExtension.class)
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @SneakyThrows
    @Override
    public void beforeEach(ExtensionContext context) {
        LOCK.lock();
        Map<Parameter, StaticUser> allocatedUsers = new HashMap<>();
        try {
            Parameter[] parameters = context.getRequiredTestMethod().getParameters();
            Map<Parameter, Type> neededTypeByParameter = Arrays.stream(parameters)
                    .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class) && p.getType() == StaticUser.class)
                    .collect(Collectors.toMap(Function.identity(), this::getUserType));

            Map<Type, Long> requiredUserTypes = neededTypeByParameter.values().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            StopWatch sw = StopWatch.createStarted();
            boolean canAllocateUsers = canAllocateUsers(requiredUserTypes);
            while (!canAllocateUsers && sw.getTime(TimeUnit.SECONDS) < 2) {
                Thread.sleep(50);
                canAllocateUsers = canAllocateUsers(requiredUserTypes);
            }

            if (canAllocateUsers) {
                for (Map.Entry<Parameter, Type> entry : neededTypeByParameter.entrySet()) {
                    StaticUser user = USER_POOLS.get(entry.getValue()).poll();
                    if (user != null) {
                        allocatedUsers.put(entry.getKey(), user);
                    }
                }
            }

            if (allocatedUsers.size() != neededTypeByParameter.size()) {
                throw new IllegalStateException("Can't obtain user after 30s.");
            }

            Allure.getLifecycle().updateTestCase(testCase ->
                    testCase.setStart(new Date().getTime())
            );
            context.getStore(NAMESPACE).put(context.getUniqueId(), allocatedUsers);
        } catch (Throwable e) {
            retrieveToUserPools(allocatedUsers);
            throw e;
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Map<Parameter, StaticUser> allocatedUsers = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        retrieveToUserPools(allocatedUsers);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Map<Parameter, StaticUser> allocatedUsers = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        return allocatedUsers.get(parameterContext.getParameter());
    }

    private void retrieveToUserPools(Map<Parameter, StaticUser> allocatedUsers) {
        if (allocatedUsers != null) {
            allocatedUsers.forEach((param, user) -> {
                Type type = getUserType(param);
                USER_POOLS.get(type).add(user);
            });
        }
    }

    private Type getUserType(Parameter param) {
        return param.getAnnotation(UserType.class).value();
    }

    private boolean canAllocateUsers(Map<Type, Long> requiredUserTypes) {
        return requiredUserTypes.entrySet().stream()
                .allMatch(entry -> USER_POOLS.get(entry.getKey()).size() >= entry.getValue());
    }

}
