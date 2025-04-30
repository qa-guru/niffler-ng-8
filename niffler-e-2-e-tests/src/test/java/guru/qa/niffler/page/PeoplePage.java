package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage {

    private final SelenideElement peopleTab = $("a[href='/people/friends']");
    private final SelenideElement allTab = $("a[href='/people/all']");
    private final SelenideElement peopleTable = $("#all");

    private SelenideElement findUserRow(String username) {
        return peopleTable.$$("tr").find(text(username));
    }

    public PeoplePage checkInvitationSentToUser(String username) {
        findUserRow(username).shouldHave(text("Waiting..."));
        return this;
    }

    public PeoplePage sendFriendRequestTo(String username) {
        SelenideElement button = findUserRow(username).$("button");
        button.shouldHave(text("Add friend")).click();
        return this;
    }
}
