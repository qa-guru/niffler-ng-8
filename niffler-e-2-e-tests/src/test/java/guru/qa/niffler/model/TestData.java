package guru.qa.niffler.model;

import java.util.List;

public record TestData(
    String password,
    List<CategoryJson> categories,
    List<SpendJson> spends,
    List<UserJson> friends,
    List<UserJson> outcomeInvitations,
    List<UserJson> incomeInvitations
) {
}