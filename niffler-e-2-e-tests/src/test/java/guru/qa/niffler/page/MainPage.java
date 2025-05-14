package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement historyBox = $("#spendings");
    private final SelenideElement statisticsBox = $("#stat");
    private final SelenideElement searchInput = $("input");
    private final SelenideElement profileImage = $(".MuiAvatar-img");
    private final SelenideElement deleteBtn = $("#delete");
    private final SelenideElement dialogWindow = $("div[role='dialog']");
    private final SelenideElement contextMenuInAvatarBtn = $("button[aria-label='Menu']");
    private final ElementsCollection contextMenuElements = $$(".MuiList-padding li");

    @Step("Изменить spending с description {0}")
    public EditSpendingPage editSpending(String spendingDescription) {
        searchInput.setValue(spendingDescription).pressEnter();
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public EditSpendingPage editSpending(UserJson user, int i) {
        return editSpending(user.testData().spendings().get(i).description());
    }

    @Step("Удалить spending с description {0}")
    public MainPage deleteSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(0)
                .click();
        deleteBtn.click();
        dialogWindow.$(byText("Delete")).click();
        return new MainPage();
    }

    @Step("Check that table contains description {0}")
    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    @Step("Assert main components")
    public MainPage assertMainComponents(){
        historyBox.shouldBe(visible, Duration.ofSeconds(10));
        statisticsBox.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @Step("Go to Profile")
    public ProfilePage goToProfile() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Go to Friends")
    public FriendsPage goToFriendsList() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Go to All People")
    public AllPeoplePage goToAllPeopleList() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("All People")).click();
        return new AllPeoplePage();
    }

    public MainPage search(String spend) {
        searchInput.sendKeys(spend);
        searchInput.sendKeys(Keys.ENTER);
        return this;
    }

    public StatComponent getStatComponent(){
        return new StatComponent();
    }

}
