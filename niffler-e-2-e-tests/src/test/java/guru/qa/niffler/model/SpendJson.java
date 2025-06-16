package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.enums.CurrencyValues;


import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public record SpendJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("spendDate")
        Date spendDate,
        @JsonProperty("category")
        CategoryJson category,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("amount")
        Double amount,
        @JsonProperty("description")
        String description,
        @JsonProperty("username")
        String username) {


    public static @Nonnull SpendJson fromEntity(@Nonnull SpendEntity entity) {
        final CategoryEntity category = entity.getCategory();
        final String username = entity.getUsername();

        return new SpendJson(
                entity.getId(),
                entity.getSpendDate(),
                new CategoryJson(
                        category.getId(),
                        category.getName(),
                        username,
                        category.isArchived()
                ),
                entity.getCurrency(),
                entity.getAmount(),
                entity.getDescription(),
                username
        );
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public Date spendDate() {
        return spendDate;
    }

    @Override
    public CategoryJson category() {
        return category;
    }

    @Override
    public CurrencyValues currency() {
        return currency;
    }

    @Override
    public Double amount() {
        return amount;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String username() {
        return username;
    }
}
