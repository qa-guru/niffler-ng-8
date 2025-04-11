package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement profileBtn = $("[data-testid='PersonIcon']");
    private final SelenideElement profileLink = $(By.linkText("Profile"));
    private final ElementsCollection menuItems = $$("ul[role='menu'] li");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    public void checkMainPageShouldBeLoaded() {
        checkStatisticsShouldBeLoaded();
        checkHistoryOfSpendingShouldBeLoaded();
    }

    public void checkStatisticsShouldBeLoaded() {
        $$("h2")
                .get(0)
                .shouldHave(text("Statistics"));
    }

    public void checkHistoryOfSpendingShouldBeLoaded() {
        $$("h2")
                .get(1)
                .shouldHave(text("History of Spendings"));
    }

    public ProfilePage goToProfilePage() {
        profileBtn
                .click();
        profileLink
                .click();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsTab() {
        profileBtn
                .click();
        menuItems
                .findBy(text("Friends"))
                .click();
        return new FriendsPage();
    }

    public AllPeoplePage goToAllPeopleTab() {
        profileBtn
                .click();
        menuItems
                .findBy(text("All People"))
                .click();
        return new AllPeoplePage();
    }


}
