package guru.qa.niffler.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage {

    private final SelenideElement friendsTableContainer = $("#simple-tabpanel-friends");
    private final SelenideElement friendsTable = friendsTableContainer.$("#friends");
    private final SelenideElement noFriendsText = friendsTableContainer.$("p.MuiTypography-h6");
    private final SelenideElement lonelyNifflerIng = friendsTableContainer.$("img[alt='Lonely niffler']");
    private final ElementsCollection tableHeaders = friendsTableContainer.$$("h2.MuiTypography-h5");
    private final SelenideElement friendRequestTable = friendsTableContainer.$("#requests");
    private final SelenideElement allPeopleTable = $("a[href='/people/all']");
    private final SelenideElement searchInput = $("input[placeholder='Search");
    private final SelenideElement allTable = $("#all");


    public FriendsPage clickAllPeopleTab() {
        allPeopleTable.click();
        return this;
    }

    public FriendsPage findUsername(String friendName) {
        searchInput.setValue(friendName);
        searchInput.sendKeys(Keys.ENTER);
        return this;
    }

    public FriendsPage checkFriendTableContainsName(String expName) {
        tableHeaders.shouldHave(texts("My friends"));
        SelenideElement row = friendsTable.$$("tr")
                .shouldHave(size(1))
                .find(text(expName))
                .shouldBe(visible);
        row.$("button.MuiButton-containedSecondary")
                .shouldHave(text("Unfriend"));
        return this;
    }

    public FriendsPage checkFriendRequestTableContainsName(String expName) {
        tableHeaders.shouldHave(texts("Friend requests"));
        SelenideElement row = friendRequestTable.$$("tr")
                .shouldHave(size(1))
                .find(text(expName))
                .shouldBe(visible);
        row.$("button.MuiButton-containedPrimary")
                .shouldHave(text("Accept"));
        row.$("button.MuiButton-containedSecondary")
                .shouldHave(text("Decline"));
        return this;
    }

    public FriendsPage checkEmptyFriends() {
        friendsTable.shouldNotBe(Condition.exist);
        noFriendsText.shouldBe(visible);
        lonelyNifflerIng.shouldBe(visible);
        return this;
    }

    public FriendsPage checkAllTableContainsOutcomeInvitationWithName(String expName) {
        SelenideElement row = allTable.$$("tr")
                .find(text(expName))
                .shouldBe(visible);
        row.$("span")
                .shouldHave(text("Waiting..."));
        return this;
    }

}
