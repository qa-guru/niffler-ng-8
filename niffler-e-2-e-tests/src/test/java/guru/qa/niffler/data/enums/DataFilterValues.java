package guru.qa.niffler.data.enums;

public enum DataFilterValues {
    ALL_TIME("All time"),
    LAST_MONTH("Last month"),
    LAST_WEEK("Last week"),
    TODAY("Today");


    private final String nameMonth;

    DataFilterValues(String nameMonth) {
        this.nameMonth = nameMonth;
    }

    public String getNameMonth() {
        return nameMonth;
    }
}