package guru.qa.niffler.api.model;

import java.util.Date;
import java.util.List;

public record StatisticJson(Date dateFrom,
                            Date dateTo,
                            CurrencyValues currency,
                            Double total,
                            CurrencyValues userDefaultCurrency,
                            Double totalInUserDefaultCurrency,
                            List<StatisticByCategoryJson> categoryStatistics) {
}