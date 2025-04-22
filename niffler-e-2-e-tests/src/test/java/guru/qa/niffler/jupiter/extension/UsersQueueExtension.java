package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
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
        EMPTY_USERS.add(new StaticUser("Grisha", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "Masha", null, null)); //у него друг Masha
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("Alex", "12345", null, "Lena", null)); //получено приглашение от Lena
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("Lena", "12345", null, null, "Alex")); //отправлено приглашение Alex
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;
        enum Type {
            EMPTY,
            WITH_FRIEND,
            WITH_INCOME_REQUEST,
            WITH_OUTCOME_REQUEST
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
              .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class) && p.getType().isAssignableFrom(StaticUser.class))
              .map(p -> p.getAnnotation(UserType.class))
              .forEach(
                      ut -> {
                          Optional<StaticUser> user = Optional.empty();
                          StopWatch sw = StopWatch.createStarted();
                          while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                              user = switch (ut.value()) {
                                  case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                                  case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                                  case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                                  case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                              };
                          }
                          Allure.getLifecycle().updateTestCase(testCase -> {
                              testCase.setStart(new Date().getTime());
                          });
                          user.ifPresentOrElse(
                                  u ->
                                          ((Map<UserType, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                                                  context.getUniqueId(),
                                                  key -> new HashMap<>()
                                          )).put(ut, u),
                                  () -> {
                                      throw new IllegalStateException("Can't find user after 30 sec");
                                  }
                          );
                      });
    }

        @Override
        @SuppressWarnings("unchecked")
        public void afterEach (ExtensionContext context){
            Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
            if (map != null) {
                for (Map.Entry<UserType, StaticUser> e : map.entrySet()) {
                    switch (e.getKey().value()) {
                        case EMPTY -> EMPTY_USERS.add(e.getValue());
                        case WITH_FRIEND -> WITH_FRIEND_USERS.add(e.getValue());
                        case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(e.getValue());
                        case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(e.getValue());
                    }
                }
            }
        }

        @Override
        public boolean supportsParameter (ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
            return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                    && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
        }

        @Override
        public StaticUser resolveParameter (ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
            return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
                    .get(AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).get());
        }
    }
