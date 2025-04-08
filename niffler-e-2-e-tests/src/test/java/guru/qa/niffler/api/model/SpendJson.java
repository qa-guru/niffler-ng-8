package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

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

    public static SpendJson fromEntity(SpendEntity entity) {
        final CategoryEntity category = entity.getCategory();
        final String username = entity.getUsername();

        return new SpendJson(
                entity.getId(),
                entity.getSpendDate(),
                CategoryJson.fromEntity(category),
                entity.getCurrency(),
                entity.getAmount(),
                entity.getDescription(),
                username
        );
    }

}
