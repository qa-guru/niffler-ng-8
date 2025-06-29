package guru.qa.niffler.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.web.component.Alert;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage {

    private static final String ACCEPT_FRIENDSHIP_BTN = "button.MuiButton-containedPrimary";
    private static final String DECLINE_FRIENDSHIP_BTN = "button.MuiButton-containedSecondary";
    private static final String UNFRIEND_BTN = "button.MuiButton-containedSecondary";

    @Getter
    private final Alert<FriendsPage> alert = new Alert<>(this);

    private final SelenideElement friendsTableContainer = $("#simple-tabpanel-friends");
    private final SelenideElement friendsTable = friendsTableContainer.$("#friends");
    private final SelenideElement noFriendsText = friendsTableContainer.$("p.MuiTypography-h6");
    private final SelenideElement lonelyNifflerIng = friendsTableContainer.$("img[alt='Lonely niffler']");
    private final ElementsCollection tableHeaders = friendsTableContainer.$$("h2.MuiTypography-h5");
    private final SelenideElement friendRequestTable = friendsTableContainer.$("#requests");
    private final ElementsCollection friendRequestRow = friendsTableContainer.$$("#requests tr");
    private final SelenideElement allPeopleTable = $("a[href='/people/all']");
    private final SelenideElement searchInput = $("input[placeholder='Search");
    private final SelenideElement allTable = $("#all");

    @Step("Открываем список всех пользователей")
    public FriendsPage clickAllPeopleTab() {
        allPeopleTable.click();
        return this;
    }

    @Step("Ищем пользователя по имени")
    public FriendsPage findUsername(String friendName) {
        searchInput.setValue(friendName);
        searchInput.sendKeys(Keys.ENTER);
        return this;
    }

    @Step("Проверяем наличие имени в таблице")
    public FriendsPage checkFriendTableContainsName(String expName) {
        findUsername(expName);
        tableHeaders.shouldHave(texts("My friends"));
        SelenideElement row = friendsTable.$$("tr")
            .shouldHave(size(1))
            .find(text(expName))
            .shouldBe(visible);
        row.$(UNFRIEND_BTN)
            .shouldHave(text("Unfriend"));
        return this;
    }

    @Step("Проверяем наличие имени в таблице входящих запросов на дружбу")
    public FriendsPage checkFriendRequestTableContainsName(String expName) {
        tableHeaders.shouldHave(texts("Friend requests"));
        SelenideElement row = friendRequestTable.$$("tr")
            .shouldHave(size(1))
            .find(text(expName))
            .shouldBe(visible);
        row.$(ACCEPT_FRIENDSHIP_BTN)
            .shouldHave(text("Accept"));
        row.$(DECLINE_FRIENDSHIP_BTN)
            .shouldHave(text("Decline"));
        return this;
    }

    @Step("Кликаем на принятие входящей заявки на дружбу")
    public FriendsPage clickAcceptBtnForName(String expName) {
        friendRequestRow.find(text(expName))
            .$(ACCEPT_FRIENDSHIP_BTN)
            .click();
        return this;
    }

    @Step("Кликаем на принятие входящей заявки на дружбу")
    public FriendsPage clickDeclineBtnForName(String expName) {
        friendRequestRow.find(text(expName))
            .$(DECLINE_FRIENDSHIP_BTN)
            .click();
        return this;
    }

    @Step("Проверяем что таблица с друзьями пуста")
    public FriendsPage checkEmptyFriends() {
        friendsTable.shouldNotBe(Condition.exist);
        noFriendsText.shouldBe(visible);
        lonelyNifflerIng.shouldBe(visible);
        return this;
    }

    @Step("Проверяем наличие имени в таблице исходящих запрсов на дружбу")
    public FriendsPage checkAllTableContainsOutcomeInvitationWithName(String expName) {
        SelenideElement row = allTable.$$("tr")
            .find(text(expName))
            .shouldBe(visible);
        row.$("span")
            .shouldHave(text("Waiting..."));
        return this;
    }
}
