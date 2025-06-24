package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.web.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Header<P> extends BaseComponent<P> {

    private final SelenideElement self = $("#root header");
    private final SelenideElement addSpendingBtn = self.$("a[href='/spending']");
    private final SelenideElement icon = self.$("h1");
    private final SelenideElement profileMenuBtn = self.$("button[aria-label='Menu']");
    private final SelenideElement profileMenu = $("ul[role='menu']");
    private final SelenideElement profileMenuItem = profileMenu.$("a[href='/profile']");
    private final SelenideElement friendsMenuItem = profileMenu.$("a[href='/people/friends']");
    private final SelenideElement peopleAllMenuItem = profileMenu.$("a[href='/people/all']");
    private final SelenideElement signOutBtn = profileMenu.$("li .MuiListItemIcon-root img[src='/src/assets/icons/ic_signout.svg']");

    public Header(P currentPage) {
        super(currentPage, $("#root header"));
    }

    @Step("переходим в профиль")
    public ProfilePage goToProfilePage() {
        profileMenuBtn.click();
        profileMenuItem.click();
        return new ProfilePage();
    }

    @Step("Переходим на страницу с друзьями")
    public FriendsPage goToFriendsPage() {
        profileMenuBtn.click();
        friendsMenuItem.click();
        return new FriendsPage();
    }

    @Step("Переходим на страницу со всеми людьми")
    public FriendsPage goToAllPeoplePage() {
        profileMenuBtn.click();
        peopleAllMenuItem.click();
        return new FriendsPage();
    }

    @Step("Переходим на главную страницу")
    public MainPage goToMainPage() {
        icon.click();
        return new MainPage();
    }

    @Step("Переходим добавления траты")
    public SpendingPage addNewSpending() {
        addSpendingBtn.click();
        return new SpendingPage();
    }

    @Step("Разлогиниваемся")
    public LoginPage signOut() {
        signOutBtn.click();
        return new LoginPage();
    }
}
