package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    private final ElementsCollection friendsAndPeopleTab = $$("[aria-label='People tabs'] a");
    private final SelenideElement friendsList = $("#simple-tabpanel-friends");
    private final SelenideElement nifflerLogo = $("[alt='Lonely niffler']");
    private final ElementsCollection friends = $$("#simple-tabpanel-friends #friends");
    private final ElementsCollection requests = $$("#simple-tabpanel-friends #requests");
    private final ElementsCollection allPeople = $$("#simple-tabpanel-all tr");

    public FriendsPage friendsTabSubmit() {
        friendsAndPeopleTab.get(0)
                .click();
        return this;
    }

    public FriendsPage allPeopleTabSubmit() {
        friendsAndPeopleTab.get(1)
                .click();
        return this;
    }

    public FriendsPage shouldFriendVisible(String username) {
        friends.find(text(username))
                .shouldBe(visible);
        return this;
    }

    public void shouldUnfriendButtonVisible(String username) {
        friends.find(text(username))
            .$("[class*='MuiTouchRipple']")
            .shouldBe(visible);
    }

    public void shouldFriendsNotVisible() {
        friendsList.shouldHave(text("There are no users yet"));
        nifflerLogo.shouldBe(visible);
    }

    public void shouldInviteVisible(String username) {
        allPeople.find(text(username))
                .$("[class*='MuiChip-filled']")
                .shouldBe(visible);
    }

    public FriendsPage shouldAcceptButtonVisibleAndEnabled(String username) {
        requests.find(text(username))
                .$("[class*='MuiButton-containedPrimary']")
                .shouldBe(visible)
                .shouldBe(enabled);

        return this;
    }

    public void shouldDeclineButtonVisibleAndEnabled(String username) {
        requests.find(text(username))
                .$("[class*='MuiButton-containedSecondary']")
                .shouldBe(visible)
                .shouldBe(enabled);
    }
}
