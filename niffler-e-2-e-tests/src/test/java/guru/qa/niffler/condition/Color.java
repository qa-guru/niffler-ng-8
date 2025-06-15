package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Color {

    YELLOW("rgba(255, 183, 3, 1)"),
    GREEN("rgba(53, 173, 123, 1)"),
    COLOR_NOT_FOUND("COLOR_NOT_FOUND");

    private static final List<Color> colors = Arrays.asList(values());
    private final String rgb;

    @Override
    public String toString() {
        return rgb;
    }

    public static Color of(String rgba) {
        return colors.stream()
            .filter(c -> c.rgb.equals(rgba))
            .findFirst()
            .orElse(COLOR_NOT_FOUND);
    }
}
