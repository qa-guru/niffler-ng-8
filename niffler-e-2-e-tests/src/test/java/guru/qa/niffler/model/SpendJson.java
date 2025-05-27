package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.utils.RandomDataUtils;

import javax.annotation.Nonnull;
import java.util.Date;
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

    @Nonnull
    public static SpendJson fromEntity(@Nonnull SpendEntity entity) {
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

    @Nonnull
    public static SpendJson spendJson(String username, String categoryName){
        return new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                        null,
                        categoryName,
                        username,
                        false
                ),
                CurrencyValues.RUB,
                100.0,
                "test desc",
                RandomDataUtils.randomUsername()
        );
    }

    @Nonnull
    public SpendJson toUI(){
        return new SpendJson(
                null,
                this.spendDate,
                new CategoryJson(
                        null,
                        this.category.name(),
                        null,
                        false
                ),
                this.currency,
                this.amount,
                this.description,
                null
        );
    }
}
