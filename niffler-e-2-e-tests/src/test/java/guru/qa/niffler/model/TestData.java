package guru.qa.niffler.model;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;

import java.util.ArrayList;
import java.util.List;

public record TestData(String password,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings,
                       List<UserJson> friends,
                       List<UserJson> incomeInvitations,
                       List<UserJson> outcomeInvitations) {

    public TestData(String password) {
        this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public TestData(String password, List<CategoryJson> categories, List<SpendJson> spendings) {
        this(password, categories, spendings, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}