package guru.qa.niffler.model;

import java.util.Date;
import java.util.UUID;

public record SpendJson(
    UUID id,
    Date spendDate,
    CategoryJson category,
    CurrencyValues currency,
    Double amount,
    String description,
    String username) {

}
