package guru.qa.niffler.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CurrencyValues {
  RUB("₽", "RUB"),
  USD("$", "USD"),
  EUR("€", "EUR"),
  KZT("₸", "KZT");

  private final String symbol;
  private final String dataValue;
}
