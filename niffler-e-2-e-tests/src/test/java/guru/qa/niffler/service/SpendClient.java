package guru.qa.niffler.service;

import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendClient {
    SpendJson createSpend(SpendJson spend);
    void deleteSpend(SpendJson spend);
    CategoryJson createCategory(CategoryJson category);
    void deleteCategory(CategoryJson category);
    Optional<SpendJson> findById(UUID spendId);
    List<Currency> getAllCurrencies();
    double exchange(double spend, CurrencyValues spendCurrency, CurrencyValues desiredCurrency);
}
