package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    private final String NO_FRIENDS_TEXT = "There are no users yet";


    private static final ElementsCollection requestsTable = $$("#requests tr");
    private static final ElementsCollection friendsTable = $$("#friends tr");
    private static final SelenideElement friendsTabPanel = $("#simple-tabpanel-friends");

    public void verifyUserVisibleInRequests(String username) {
        requestsTable
                .findBy(text(username))
                .shouldBe(visible);
    }

    public void verifyUserVisibleInFriends(String username) {
        friendsTable
                .findBy(text(username))
                .shouldBe(visible);
    }

    public void verifyFriendsListIsEmpty() {
        friendsTabPanel.shouldHave(text(NO_FRIENDS_TEXT));
    }

}
