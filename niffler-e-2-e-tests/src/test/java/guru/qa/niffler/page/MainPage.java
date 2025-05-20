package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.spend.SpendsTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class MainPage extends BasePage {

    private final ElementsCollection tableRows;
    private final SelenideElement historyBox;
    private final SelenideElement statisticsBox;
    private final SelenideElement searchInput;
    private final SelenideElement profileImage;
    private final SelenideElement deleteBtn;
    private final SelenideElement dialogWindow ;
    private final SelenideElement contextMenuInAvatarBtn;
    private final ElementsCollection contextMenuElements;

    public MainPage(SelenideDriver driver){
        super(driver);
        this.tableRows = $$("#spendings tbody tr");
        this.historyBox = $("#spendings");
        this.statisticsBox = $("#stat");
        this.searchInput = $("input");
        this.profileImage = $(".MuiAvatar-img");
        this.deleteBtn = $("#delete");
        this.dialogWindow = $("div[role='dialog']");
        this.contextMenuInAvatarBtn = $("button[aria-label='Menu']");
        this.contextMenuElements = $$(".MuiList-padding li");
    }

    public MainPage(){
        this(null);
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
        return new ProfilePage(driver());
    }

    @Step("Go to Friends")
    public FriendsPage goToFriendsList() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("Friends")).click();
        return new FriendsPage(driver);
    }

    @Step("Go to All People")
    public AllPeoplePage goToAllPeopleList() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("All People")).click();
        return new AllPeoplePage(driver);
    }

    public MainPage search(String spend) {
        searchInput.sendKeys(spend);
        searchInput.sendKeys(Keys.ENTER);
        return this;
    }

    public StatComponent getStatComponent(){
        return driver == null
                ? new StatComponent()
                : new StatComponent(driver);
    }


    public SpendsTable getSpendTable(){
        return driver == null
                ? new SpendsTable()
                : new SpendsTable(driver);
    }
}
