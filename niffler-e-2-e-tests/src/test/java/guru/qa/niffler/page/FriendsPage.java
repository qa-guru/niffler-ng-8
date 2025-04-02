package guru.qa.niffler.page;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.model.ElementType.BUTTON;

public class FriendsPage extends BasePage {
    private final SelenideElement allPeopleTab = $(byText("All people"));
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final ElementsCollection friendsTableRows = $$("#friends tr");
    private final SelenideElement noFriendsText = $(byText("There are no users yet"));
    private final SelenideElement lonelyNifflerImage = $("img[alt='Lonely niffler']");

    private static final String ACCEPT_FRIEND_BUTTON_XPATH = ".//button[text()='Accept']";
    private static final String DECLINE_FRIEND_BUTTON_XPATH = ".//button[text()='Decline']";
    private static final String UNFRIEND_BUTTON_XPATH = ".//button[text()='Unfriend']";
    private static final String LONELY_NIFFLER_IMG_URL = "assets/niffler-with-a-coin-Cb77k8MX.png";


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
        return new AllPeoplePage();
    }

    public FriendsPage assertFriendRequestExist(String username){
        SelenideElement targetRow = requestsTableRows.find(text(username));
        targetRow.$x(ACCEPT_FRIEND_BUTTON_XPATH)
                .shouldBe(visible)
                .shouldHave(BUTTON.assertType());
        targetRow.$x(DECLINE_FRIEND_BUTTON_XPATH)
                .shouldBe(visible)
                .shouldHave(BUTTON.assertType());
        return this;
    }
    public FriendsPage assertFriendExist(String username){
        SelenideElement targetRow = friendsTableRows.find(text(username));
        targetRow.$x(UNFRIEND_BUTTON_XPATH)
                .shouldBe(visible)
                .shouldHave(BUTTON.assertType());
        return this;
    }

}
