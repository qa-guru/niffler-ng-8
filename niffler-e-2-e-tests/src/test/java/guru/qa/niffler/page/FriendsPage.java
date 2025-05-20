package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static guru.qa.niffler.model.ElementType.BUTTON;

public class FriendsPage extends BasePage {

    private static final String LONELY_NIFFLER_IMG_URL = "assets/niffler-with-a-coin-Cb77k8MX.png";

    // Элементы страницы
    private final SelenideElement allPeopleTab;
    private final ElementsCollection requestsTableRows;
    private final ElementsCollection friendsTableRows;
    private final SelenideElement noFriendsText;
    private final SelenideElement lonelyNifflerImage;
    private final SelenideElement searchInput;

    public FriendsPage(SelenideDriver driver) {
        super(driver);
        this.allPeopleTab = $(byText("All people"));
        this.requestsTableRows = $$("#requests tr");
        this.friendsTableRows = $$( "#friends tr");
        this.noFriendsText = $(byText("There are no users yet"));
        this.lonelyNifflerImage = $("img[alt='Lonely niffler']");
        this.searchInput = $("input");
    }

    public FriendsPage() {
        this(null);
    }

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
    public AllPeoplePage clickAllPeopleTab(){
        allPeopleTab.click();
        return new AllPeoplePage(driver);
    }

    public FriendsPage assertFriendRequestExist(String username){
        searchInput.setValue(username).pressEnter();
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

}
