package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    private final String NO_FRIENDS_TEXT = "There are no users yet";
    private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement friendsTable = $("#friends");
    private final SelenideElement friendsTabPanel = $("#simple-tabpanel-friends");
    private final SelenideElement searchField = $("input[placeholder='Search']");

    @Nonnull
  public FriendsPage checkExistingFriends(String... expectedUsernames) {
    friendsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

    @Nonnull
  public FriendsPage checkNoExistingFriends() {
    friendsTable.$$("tr").shouldHave(size(0));
    return this;
  }

    @Nonnull
  public FriendsPage checkExistingInvitations(String... expectedUsernames) {
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }
    public void verifyUserVisibleInRequests(String username) {
        findUser(username);
        requestsTable
                .$$("tr")
                .findBy(text(username))
                .shouldBe(visible);
    }

    public void verifyUserVisibleInFriends(String username) {
        findUser(username);
        friendsTable
                .$$("tr")
                .findBy(text(username))
                .shouldBe(visible);
    }

    public void verifyFriendsListIsEmpty() {
        friendsTabPanel.shouldHave(text(NO_FRIENDS_TEXT));
    }

    public void findUser(String username) {
        searchField
                .setValue(username)
                .pressEnter();
    }
}
