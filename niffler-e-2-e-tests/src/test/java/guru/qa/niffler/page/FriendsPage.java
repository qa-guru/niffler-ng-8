package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    private final ElementsCollection friendsAndPeopleTab = $$("[aria-label='People tabs'] a");
    private final ElementsCollection friends = $$("#simple-tabpanel-friends #friends");

    public FriendsPage friendsTabSubmit() {
        friendsAndPeopleTab.get(0).click();

        return this;
    }

    public FriendsPage allPeopleTabSubmit() {
        friendsAndPeopleTab.get(1).click();

        return this;
    }

    public FriendsPage shouldFrienVisible(String username) {
        friends.find(text(username)).shouldBe(visible);

        return this;
    }
}
