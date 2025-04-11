package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    //todo переписать на xpath
    private final ElementsCollection friendNames = $$("#friends");
    private final ElementsCollection friendRequest = $$("#requests");
    private final SelenideElement lonelyNifflerImg = $("img[alt='Lonely niffler']");

    public FriendsPage checkFriendExistsInList(String username) {
        friendNames.find(text(username)).shouldBe(visible);
        return this;
    }

    public FriendsPage checkNoFriendsInList() {
        friendNames.shouldHave(size(0));
        lonelyNifflerImg.shouldBe(visible);
        return this;
    }

    public FriendsPage checkFriendRequest(String username) {
        friendRequest.find(text(username)).shouldBe(visible);
        return this;
    }
}
