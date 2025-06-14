package guru.qa.niffler.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CurrencyValues {
  RUB("₽"), USD("$"), EUR("€"), KZT("₸");

  final String symbol;
}
