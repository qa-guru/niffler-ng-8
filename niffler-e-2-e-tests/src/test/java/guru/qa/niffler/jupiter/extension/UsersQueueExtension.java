package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
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
    EMPTY_USERS.add(new StaticUser("alice", "qwer", null, null, null));
    WITH_FRIEND_USERS.add(new StaticUser("emily", "qwer", "kevin", null, null));
    WITH_FRIEND_USERS.add(new StaticUser("kevin", "qwer", "emily", null, null));
    WITH_INCOME_REQUEST_USERS.add(new StaticUser("oscar", "qwer", null, "mario", null));
    WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("mario", "qwer", null, null, "oscar"));
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
  @SuppressWarnings("unchecked")
  public void beforeTestExecution(ExtensionContext context) {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
      .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
      .map(param -> param.getAnnotation(UserType.class))
      .forEach(userType -> {
        Optional<StaticUser> user = Optional.empty();
        StopWatch sw = StopWatch.createStarted();
        while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
          user = Optional.ofNullable(getUserQueue(userType).poll());
        }
        Allure.getLifecycle().updateTestCase(testCase ->
          testCase.setStart(new Date().getTime())
        );
        user.ifPresentOrElse(
          u -> {
            Map<UserType, StaticUser> users = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
              .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());
            users.put(userType, u);
          },
          () -> {
            throw new IllegalStateException("Can`t obtain user after 30s.");
          }
        );
      });
  }

  @Override
  @SuppressWarnings("unchecked")
  public void afterTestExecution(ExtensionContext context) {
    Map<UserType, StaticUser> users = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
    if (users != null) {
      users.forEach((userType, user) ->
        getUserQueue(userType).add(user));
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
      && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    UserType userType = parameterContext.findAnnotation(UserType.class)
      .orElseThrow(() -> new ParameterResolutionException("@UserType annotation not found"));
    return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class).get(userType);
  }

  private Queue<StaticUser> getUserQueue(UserType userType) {
    return switch (userType.value()) {
      case EMPTY -> EMPTY_USERS;
      case WITH_FRIEND -> WITH_FRIEND_USERS;
      case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
      case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
    };
  }
}
