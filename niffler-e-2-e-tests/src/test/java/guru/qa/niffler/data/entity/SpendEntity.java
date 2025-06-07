package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class SpendEntity implements Serializable {
  private UUID id;
  private String username;
  private CurrencyValues currency;
  private Date spendDate;
  private Double amount;
  private String description;
  private CategoryEntity category;

  public static SpendEntity fromJson(SpendJson json) {
    SpendEntity spend = new SpendEntity();
    return spend;
  }
}
