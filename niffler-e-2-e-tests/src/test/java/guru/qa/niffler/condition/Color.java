package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Color {

    YELLOW("rgba(255, 183, 3, 1)"),
    GREEN("rgba(53, 173, 123, 1)"),
    ORANGE("rgba(251, 133, 0, 1)"),
    BLUE_100("rgba(41, 65, 204, 1)"),
    AZURE("rgba(33, 158, 188, 1)"),
    BLUE_200("rgba(22, 41, 149, 1)"),
    RED("rgba(247, 89, 67, 1)"),
    SKY_BLUE("rgba(99, 181, 226, 1)"),
    PURPLE("rgba(148, 85, 198, 1)"),
    COLOR_NOT_FOUND("COLOR_NOT_FOUND");

    private static final List<Color> colors = Arrays.asList(values());
    private final String rgb;

    @Override
    public String toString() {
        return rgb;
    }

    public static @Nonnull Color of(@Nullable String rgba) {
        return colors.stream()
            .filter(c -> c.rgb.equals(rgba))
            .findFirst()
            .orElse(COLOR_NOT_FOUND);
    }
}
