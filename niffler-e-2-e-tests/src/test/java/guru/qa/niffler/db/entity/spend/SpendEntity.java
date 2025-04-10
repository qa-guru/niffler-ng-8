package guru.qa.niffler.db.entity.spend;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.SpendJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class SpendEntity implements Serializable {

  private UUID id;
  private String username;
  private CurrencyValues currency;
  private Date spendDate;
  private Double amount;
  private String description;
  private CategoryEntity category;

  public static SpendEntity fromJson(SpendJson json) {
    return new SpendEntity()
            .setId(json.id())
            .setSpendDate(new Date(json.spendDate().getTime()))
            .setCategory(CategoryEntity.fromJson(json.category()))
            .setCurrency(json.currency())
            .setAmount(json.amount())
            .setUsername(json.username())
            .setDescription(json.description());
  }

}
