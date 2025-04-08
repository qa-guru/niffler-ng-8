package guru.qa.niffler.db.entity.spend;

import guru.qa.niffler.api.model.CategoryJson;
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

  public SpendEntity(UUID id,
                     Date spendDate,
                     CategoryEntity category,
                     CurrencyValues currency,
                     Double amount,
                     String username,
                     String description) {
    this.id = id;
    this.spendDate = spendDate;
    this.category = category;
    this.currency = currency;
    this.amount = amount;
    this.username = username;
    this.description = description;
  }

  public static SpendEntity fromJson(SpendJson json) {
    final CategoryJson category = json.category();
    final String username = json.username();

    return new SpendEntity(
            json.id(),
            new Date(json.spendDate().getTime()),
            CategoryEntity.fromJson(category),
            json.currency(),
            json.amount(),
            json.description(),
            username
    );
  }

}
