package guru.qa.niffler.api.model;

import java.util.List;

public record StatisticByCategoryJson(String category,
                                      Double total,
                                      Double totalInUserDefaultCurrency,
                                      List<SpendJson> spends) {
}