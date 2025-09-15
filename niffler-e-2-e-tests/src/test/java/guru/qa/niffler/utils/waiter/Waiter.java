package guru.qa.niffler.utils.waiter;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static guru.qa.niffler.utils.waiter.WaiterConfig.INTERVAL;
import static guru.qa.niffler.utils.waiter.WaiterConfig.WAIT_TIME;

@UtilityClass
@ParametersAreNonnullByDefault
public final class Waiter {

  @Nullable
  public static <T, V> V get(
      Supplier<T> supplier,
      Predicate<T> condition,
      Function<T, V> extractor,
      Duration duration
  ) {
    Instant startTime = Instant.now();
    do {
      T value = supplier.get();
      if (condition.test(value)) {
        return extractor.apply(value);
      }
      try {
        Thread.sleep(INTERVAL);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } while (Instant.now().isBefore(startTime.plus(duration)));

    return switch (WaiterConfig.NOT_FOUND_STRATEGY) {
      case NULLABLE ->  null;
      case ERROR -> throw new RuntimeException("Duration time exceeded");
    };
  }

  public static <V> V getNonOptional(Supplier<Optional<V>> supplier, Duration duration) {
    return get(supplier, Optional::isPresent, Optional::get, duration);
  }

  public static <V> V getNonOptional(Supplier<Optional<V>> supplier) {
    return getNonOptional(supplier, WAIT_TIME);
  }

  public static <V> V getNonNull(Supplier<V> supplier, Duration duration) {
    return get(supplier, Objects::nonNull, v -> v, duration);
  }

  public static <V> V getNonNull(Supplier<V> supplier) {
    return getNonNull(supplier, WAIT_TIME);
  }
}

