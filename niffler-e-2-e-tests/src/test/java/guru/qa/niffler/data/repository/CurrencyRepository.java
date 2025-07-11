package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.currency.CurrencyEntity;

import java.util.List;

public interface CurrencyRepository {
    List<CurrencyEntity> getAll();
}
