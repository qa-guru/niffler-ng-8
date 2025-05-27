package guru.qa.niffler.page.component;

import com.codeborne.selenide.*;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.security.AllPermission;
import java.time.Duration;

import static com.codeborne.selenide.Selectors.byXpath;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header>{
    private final SelenideElement avatarButton = self.$(byXpath("//header//button"));
    private final ElementsCollection menuButtons = self.$$(byXpath("//ul[@role='menu']//li"));
    private final SelenideElement mainPageButton = self.$("a[href='/main']");
    private final SelenideElement newSpendingButton = self.$("a[href='/spending']");

    public Header(@Nullable SelenideDriver driver) {
        super(driver,
                driver == null
                ? Selenide.$("header")
                : driver.$("header")
        );
    }
    public Header() {
        super(Selenide.$("header"));
    }

    @Step("Open menu")
    public Header openMenu(){
        avatarButton
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
        return this;
    }


    private Header clickMenuButtonsExcludeLogout(int i){
        menuButtons.get(i).find(By.xpath("./a")).click();
        return this;
    }

    @Step("Open profile page")
    public ProfilePage toProfilePage(){
        openMenu()
                .clickMenuButtonsExcludeLogout(0);
        return new ProfilePage(driver);
    }

    @Step("Open friends page")
    public FriendsPage toFriendsPage(){
        openMenu()
                .clickMenuButtonsExcludeLogout(1);
        return new FriendsPage(driver);
    }

    @Step("Open all people page")
    public AllPeoplePage toAllPeoplePage(){
        openMenu()
                .clickMenuButtonsExcludeLogout(2);
        return new AllPeoplePage(driver);
    }

    @Step("Open main page")
    public MainPage toMainPage(){
        mainPageButton.click();
        return new MainPage(driver);
    }

    @Step("Sign out")
    public LoginPage signOut(){
        openMenu();
        menuButtons.get(3).click();
        return new LoginPage(driver);
    }

    @Step("Open add spending page")
    public EditSpendingPage addSpendingPage(){
        newSpendingButton.click();
        return new EditSpendingPage(driver);
    }

}
