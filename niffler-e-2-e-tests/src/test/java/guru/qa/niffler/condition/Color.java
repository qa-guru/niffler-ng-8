package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Color {
  yellow("rgba(255, 183, 3, 1)"), green("rgba(53, 173, 123, 1)");

  public final String rgb;

  public static Color fromRgb(String rgbValue) {
    for (Color color : values()) {
      if (color.rgb.equalsIgnoreCase(rgbValue.trim())) {
        return color;
      }
    }
    throw new IllegalArgumentException("Unknown color: " + rgbValue);
  }
}
