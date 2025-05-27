package guru.qa.niffler.page.component.spend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataFilterValues {
    ALL("All time"),
    MONTH("Last month"),
    WEEK("Last week"),
    TODAY("Today");
    private String text;
}
