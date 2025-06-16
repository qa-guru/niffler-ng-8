package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.data.enums.FriendType;
import guru.qa.niffler.page.component.basicComponents.Table;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsTable extends Table {

    private SelenideElement myFrendsTable = $x("//h2[.='My friends']//following::table");


    public FriendsTable checkTableContainsFriendWithStatus(String name, FriendType friendType) {
        String path = "//table//tr//*[contains(text(), " + name + ")]";
        switch (friendType) {
            case FRIEND -> $x(path).shouldBe(Condition.visible);
            case INCOME -> $x(path + "//following::*[text() ='Accept']").shouldBe(Condition.visible);
            case OUTCOME -> $x(path + "//following::*[text() ='Waiting...']").shouldBe(Condition.visible);
        }
        return this;
    }

    public FriendsTable applyFriendRequest(String username) {
        SelenideElement row = findRowByText(username);
        row.shouldBe(Condition.visible);
        row.$x(".//button[.='Accept']").click();
        return this;
    }

    public FriendsTable declineFriendRequest(String username) {
        SelenideElement row = findRowByText(username);
        row.shouldBe(Condition.visible);
        row.$x(".//button[.='Decline']").click();
        return this;
    }

    public FriendsTable checkFriendExist(String text) {
        SelenideElement row = $(myFrendsTable).$x(".//tr").$x(".//*[contains(text(), " + text + ")]//ancestor::tr");
        row.shouldBe(Condition.visible);
        return this;
    }

    public FriendsTable checkFriendNotExist(String text) {
        SelenideElement row = $(myFrendsTable).$x(".//tr").$x(".//*[contains(text(), " + text + ")]//ancestor::tr");
        row.shouldNotBe(Condition.visible);
        return this;
    }

    public FriendsTable unfriend(String username) {
        SelenideElement row = $(myFrendsTable).$x(".//tr").$x(".//*[contains(text(), " + username + ")]//ancestor::tr");
        row.$x(".//button[.='Unfriend']").click();
        return this;
    }

    public Table checkFriendsCount(int count){
        myFrendsTable.$$(rows).shouldBe(CollectionCondition.size(count));
            return this;
    }
}
