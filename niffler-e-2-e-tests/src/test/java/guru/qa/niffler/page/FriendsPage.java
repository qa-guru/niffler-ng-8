package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.jupiter.users.UsersQueueExtension;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsPage {

    public enum FriendType {
        FRIEND, INCOME, OUTCOME
    }

    private final ElementsCollection friendsRows = $$x("//table//tr");


    public FriendsPage checkTableSize(int count) {
        friendsRows.shouldBe(CollectionCondition.size(count));
        return this;
    }

    public FriendsPage checkTableEmpty() {
        checkTableSize(0);
        return this;
    }

    public FriendsPage checkTableContainsFriendWithStatus(String name, FriendType friendType) {
        String path = "//table//tr//*[contains(text(), " + name + ")]";
        switch (friendType) {
            case FRIEND -> $x(path).shouldBe(Condition.visible);
            case INCOME -> $x(path + "//following::*[text() ='Accept']").shouldBe(Condition.visible);
            case OUTCOME -> $x(path + "//following::*[text() ='Waiting...']").shouldBe(Condition.visible);
        }
        return this;
    }
}
