package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static guru.qa.niffler.model.ElementType.BUTTON;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    private static final String LONELY_NIFFLER_IMG_URL = "assets/niffler-with-a-coin-Cb77k8MX.png";

    // Элементы страницы
    private final SelenideElement allPeopleTab = $(byText("All people"));
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final ElementsCollection friendsTableRows = $$( "#friends tr");
    private final SelenideElement noFriendsText = $(byText("There are no users yet"));
    private final SelenideElement lonelyNifflerImage = $("img[alt='Lonely niffler']");
    private final SelenideElement declineButtonDialogWindow = $$("div[role='dialog'] button")
            .find(text("Decline"));
    @Getter
    private final SearchField searchField = new SearchField(driver);

    public FriendsPage(@Nullable SelenideDriver driver) {
        super(driver);
    }

    public FriendsPage() {
        super();
    }

    @Override
    public String getUrl() {
        return "people/friends";
    }

    @Step("assert that user haven't friends")
    public FriendsPage assertEmptyUser(){
        friendsTableRows.shouldHave(size(0));
        requestsTableRows.shouldBe(size(0));
        noFriendsText.shouldBe(visible);
        lonelyNifflerImage
                .shouldBe(visible)
                .shouldHave(attribute(
                        "src",
                        Config.getInstance().frontUrl()+LONELY_NIFFLER_IMG_URL));
        return this;
    }

    @Step("Click all peoples tab")
    public AllPeoplePage clickAllPeopleTab(){
        allPeopleTab.click();
        return new AllPeoplePage(driver);
    }

    @Step("assert request from {username} exist")
    public FriendsPage assertFriendRequestExist(String username){
        searchField.search(username);
        SelenideElement targetRow = requestsTableRows.find(text(username));
        targetRow.$x(".//button[text()='Accept']")
                .shouldBe(visible)
                .shouldHave(BUTTON.assertType());
        targetRow.$x(".//button[text()='Decline']")
                .shouldBe(visible)
                .shouldHave(BUTTON.assertType());
        return this;
    }

    public FriendsPage assertFriendRequestExist(UserJson userJson){
        for(String username : userJson
                .testData()
                .incomeInvitations()
                .stream()
                .map(UserJson::username)
                .toArray(String[]::new)) {
            assertFriendRequestExist(username);
        }
        return this;
    }

    @Step("assert friend with username {username} exist")
    public FriendsPage assertFriendExist(String username){
        SelenideElement targetRow = friendsTableRows.find(text(username));
        targetRow.$x(".//button[text()='Unfriend']")
                .shouldBe(visible)
                .shouldHave(BUTTON.assertType());
        return this;
    }
    public FriendsPage assertFriendExist(UserJson userJson){
        for(String username : userJson
                .testData()
                .friends()
                .stream()
                .map(UserJson::username)
                .toArray(String[]::new)
        ) {
            assertFriendExist(username);
        }
        return this;
    }

    public FriendsPage acceptFriend(String name){
        searchField.search(name);
        requestsTableRows
                .find(text(name))
                .$x(".//button[text()='Accept']")
                .click();
        searchField.clearIfNotEmpty();
        return assertFriendExist(name);
    }

    public FriendsPage acceptFriend(UserJson friend){
        return acceptFriend(friend.username());
    }

    public FriendsPage declineFriend(UserJson friend){
        return declineFriend(friend.username());
    }

    public FriendsPage declineFriend(String name){
        searchField.search(name);
        requestsTableRows
                .find(text(name))
                .$x(".//button[text()='Decline']")
                .click();
        declineButtonDialogWindow.shouldBe(visible).click();
        searchField.clearIfNotEmpty();
        requestsTableRows.shouldBe(CollectionCondition.noneMatch(
                "Проверка отсутствия текста '" + name + "'",
                element -> element.findElement(byText(name)).isDisplayed()
        ));
        friendsTableRows.shouldBe(CollectionCondition.noneMatch(
                "Проверка отсутствия текста '" + name + "'",
                element -> element.findElement(byText(name)).isDisplayed()
        ));
        return this;
    }

}
