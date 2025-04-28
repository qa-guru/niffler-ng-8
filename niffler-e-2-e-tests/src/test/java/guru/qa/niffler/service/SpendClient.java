package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public interface SpendClient {
    SpendJson createSpend(SpendJson spend);
    void deleteSpend(SpendJson spend);
    CategoryJson createCategory(CategoryJson category);
    void deleteCategory(CategoryJson category);
}
