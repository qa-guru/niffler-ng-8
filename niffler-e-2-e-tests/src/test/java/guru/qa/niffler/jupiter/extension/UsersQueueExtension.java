package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  public record StaticUser(String username,
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
      EMPTY_USERS.add(new StaticUser("Empty", "123", null, null, null));
      WITH_FRIEND_USERS.add(new StaticUser("withFriend", "123", "withFriend1", null, null));
      WITH_FRIEND_USERS.add(new StaticUser("withFriend1", "123", "withFriend", null, null));
      WITH_INCOME_REQUEST_USERS.add(new StaticUser("withIncomeRequest", "123", null, "withOutcomeRequest", null));
      WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("withOutcomeRequest", "123", null, null, "withIncomeRequest"));
  }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface UserType {
        Type value() default Type.EMPTY;
        @AllArgsConstructor
        enum Type {
            EMPTY(EMPTY_USERS),
            WITH_FRIEND(WITH_FRIEND_USERS),
            WITH_INCOME_REQUEST(WITH_INCOME_REQUEST_USERS),
            WITH_OUTCOME_REQUEST(WITH_OUTCOME_REQUEST_USERS);
            private final Queue<StaticUser> queue;

            private Queue<StaticUser> getQueue() {
                return queue;
            }
        }
    }


  @Override
  public void beforeTestExecution(ExtensionContext context) {
      Map<UserType, StaticUser> usersMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
              .getOrComputeIfAbsent(
                      context.getUniqueId(),
                      key -> new HashMap<>());
      Arrays.stream(context.getRequiredTestMethod().getParameters())
              .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
              .forEach(p -> {
                  UserType ut = p.getAnnotation(UserType.class);
                  Optional<StaticUser> user = Optional.empty();
                  StopWatch sw = StopWatch.createStarted();
                  while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                      user = Optional.ofNullable(ut
                              .value()
                              .getQueue()
                              .poll());
                  }
                  user.ifPresentOrElse(
                          u -> usersMap.put(ut,u),
                          () -> {
                              throw new IllegalStateException("Can`t obtain user after 30s.");
                          });
              });
      Allure.getLifecycle().updateTestCase(testCase ->
              testCase.setStart(new Date().getTime())
      );
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
      Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(
              context.getUniqueId(),
              Map.class
      );
      for (Map.Entry<UserType, StaticUser> e : map.entrySet()){
          e.getKey()
                  .value()
                  .getQueue()
                  .add(e.getValue());
      }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      UserType ut = parameterContext.findAnnotation(UserType.class)
              .orElse(null);

      Map<UserType, StaticUser> usersMap = extensionContext.getStore(NAMESPACE)
              .get(extensionContext.getUniqueId(), Map.class);

      if (usersMap == null || !usersMap.containsKey(ut)) {
          return null;
      }

      return usersMap.get(ut);
  }
}
