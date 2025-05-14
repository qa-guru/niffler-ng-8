package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.component.spend.SpendsTable;
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


    public SpendsTable getSpendTable(){
        return new SpendsTable();
    }
}
