package guru.qa.niffler.model;

import java.util.List;

public record TestData(String password,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings,
                       List<UserJson> friends,
                       List<UserJson> outcomeInvitations,
                       List<UserJson> incomeInvitations) {

  public List<String> friendsUsernames() {
    return extractUsernames(friends);
  }

  public List<String> outcomeInvitationsUsernames() {
    return extractUsernames(outcomeInvitations);
  }

  public List<String> incomeInvitationsUsernames() {
    return extractUsernames(incomeInvitations);
  }

  private List<String> extractUsernames(List<UserJson> users) {
    return users.stream().map(UserJson::username).toList();
  }
}
