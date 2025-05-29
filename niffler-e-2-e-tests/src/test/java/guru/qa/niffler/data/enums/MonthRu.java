package guru.qa.niffler.data.enums;

public enum MonthRu {
    JANUARY("Январь", 1),
    FEBRUARY("Февраль", 2),
    MARCH("Март", 3),
    APRIL("Апрель", 4),
    MAY("Май", 5),
    JUNE("Июнь", 6),
    JULY("Июль", 7),
    AUGUST("Август", 8),
    SEPTEMBER("Сентябрь", 9),
    OCTOBER("Октябрь", 10),
    NOVEMBER("Ноябрь", 11),
    DECEMBER("Декабрь", 12);

    private final String nameMonth;
    private final int numMonth;

    MonthRu(String nameMonth, int numMonth) {
        this.nameMonth = nameMonth;
        this.numMonth = numMonth;
    }

    public String getNameMonth() {
        return nameMonth;
    }

    public int getNumMonth() {
        return numMonth;
    }
}
