package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

  public static final String URL = Config.getInstance().frontUrl() + "/people/friends";

  private final SelenideElement friendsTab = $("#simple-tabpanel-friends");
  private final SelenideElement friendsTable = $("#friends");
  private final SelenideElement friendRequestsTable = $("#requests");

  public FriendsPage verifyFriendIsPresent(String friendName) {
    friendsTable.$$("tr").find(text(friendName)).shouldBe(visible);
    return this;
  }

  public FriendsPage verifyFriendsTableIsEmpty() {
    friendsTable.shouldNotBe(visible);
    friendsTab.shouldHave(text("There are no users yet"));
    return this;
  }

  public FriendsPage verifyIncomeInvitationIsPresent(String userName) {
    friendRequestsTable.$$("tr").find(text(userName)).shouldBe(visible);
    return this;
  }
}