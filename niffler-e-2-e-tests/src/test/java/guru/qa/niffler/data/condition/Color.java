package guru.qa.niffler.data.condition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Color {

    yellow("rgba(255, 183, 3, 1)"),
    green("rgba(53, 173, 123, 1)"),
    orange("rgba(251, 133, 0, 1)");

    public final String rgb;
}