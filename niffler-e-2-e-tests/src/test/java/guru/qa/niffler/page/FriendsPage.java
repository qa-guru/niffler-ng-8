package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

    private final SelenideElement friendsBtn = $("a[href='/people/friends']");
    private final SelenideElement allPeopleBtn = $("a[href='/people/all']");
    private final SelenideElement search = $("input[aria-label='search']");
    private final SelenideElement noHaveFriendsText = $("p.MuiTypography-h6");
    private final ElementsCollection friendsTable = $("tbody#friends").$$("tr");
    private final ElementsCollection allPeopleTable = $("tbody#all").$$("tr");
    private final ElementsCollection friendsRequests = $("tbody#requests").$$("tr");

    public FriendsPage loadingFriendsPage() {
        friendsBtn.shouldBe(visible);
        allPeopleBtn.shouldBe(visible);
        search.shouldBe(visible);
        return this;
    }

    public void checkFriendWithName(String name) {
        friendsTable.findBy(text(name))
                    .shouldBe(visible)
                    .$("button.MuiButton-containedSecondary").shouldHave(text("Unfriend")).shouldBe(visible);
    }

    public void checkEmptyFriendsList() {
        noHaveFriendsText.shouldHave(text("There are no users yet"));
        friendsTable.shouldBe(CollectionCondition.empty);
    }

    public void checkIncomeInvitation(String name) {
        friendsRequests.findBy(text(name))
                       .shouldBe(visible)
                       .$("button.MuiButton-containedPrimary").shouldHave(text("Accept")).shouldBe(visible);
    }

    public void checkOutcomeInvitation(String name) {
        allPeopleBtn.click();
        allPeopleTable.findBy(text(name)).shouldBe(visible).shouldHave(text("Waiting..."));
    }
}
