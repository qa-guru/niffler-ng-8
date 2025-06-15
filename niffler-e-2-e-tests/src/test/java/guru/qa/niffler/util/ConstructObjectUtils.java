package guru.qa.niffler.util;

import guru.qa.niffler.api.model.SpendJson;
import guru.qa.niffler.condition.CategoryBubble;
import guru.qa.niffler.condition.Color;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ConstructObjectUtils {

    public static List<SpendJson> expSpendings(Collection<SpendJson> spends, Object oldValue, Object newValue) {
        return spends.stream()
            .map(spend -> {
                if (oldValue != null) {
                    return copyWithReplacement(spend, oldValue, newValue);
                }
                return spend;
            })
            .toList();
    }

    public static List<CategoryBubble> expCategoryBubbles(Collection<SpendJson> spends,
                                                          Object target,
                                                          Object replacement) {
        AtomicInteger i = new AtomicInteger();
        return expSpendings(spends, target, replacement).stream()
            .sorted(
                Comparator.comparing((SpendJson s) -> s.category().name().matches("^[А-Яа-яЁё].*") ? 0 : 1)
                    .thenComparing(s -> s.category().name(), String.CASE_INSENSITIVE_ORDER)
            )
            .map(ConstructObjectUtils::formatSpend)
            .map(text -> new CategoryBubble(Color.values()[i.getAndIncrement()], text))
            .toList();
    }

    private static String formatSpend(SpendJson spend) {
        return "%s %s %s".formatted(
            spend.category().name(),
            FormatUtil.format(spend.amount()),
            spend.currency().getSymbol()
        );
    }

    @SneakyThrows
    public static <T> T copyWithReplacement(T original, Object oldValue, Object newValue) {
        if (original == null) return null;

        Class<?> clazz = original.getClass();
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Field field = clazz.getDeclaredFields()[i];
            field.setAccessible(true);
            Object value = field.get(original);

            if (Objects.equals(value, oldValue)) {
                args[i] = newValue;
            } else if (isEligibleForRecursion(value)) {
                args[i] = copyWithReplacement(value, oldValue, newValue);
            } else {
                args[i] = value;
            }
        }

        return (T) constructor.newInstance(args);
    }

    private static boolean isEligibleForRecursion(Object value) {
        if (value == null) return false;
        Class<?> clazz = value.getClass();
        return !clazz.isPrimitive()
            && !clazz.isEnum()
            && !clazz.equals(String.class)
            && !Number.class.isAssignableFrom(clazz)
            && !clazz.getPackageName().startsWith("java.");
    }

}
