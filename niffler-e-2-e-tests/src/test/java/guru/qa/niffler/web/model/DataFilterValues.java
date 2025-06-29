package guru.qa.niffler.web.model;

import lombok.Getter;

@Getter
public enum DataFilterValues {

    ALL_TIME("ALL"),
    LAST_MONTH("MONTH"),
    LAST_WEEK("WEEK"),
    TODAY("TODAY");

    private final String dataValue;

    DataFilterValues(String dataValue) {
        this.dataValue = dataValue;
    }
}
