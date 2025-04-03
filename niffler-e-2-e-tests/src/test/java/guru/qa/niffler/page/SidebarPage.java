package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class SidebarPage {

    private final SelenideElement menu = $x("//*[@data-testid='PersonIcon']");
    private final SelenideElement profile = $x("//*[contains(text(),'Profile')]");
    private final SelenideElement friends = $x("//*[contains(text(),'Friends')]");
    private final SelenideElement allPeople = $x("//*[contains(text(),'All People')]");
    private final SelenideElement signOut = $x("//*[contains(text(),'Sign Out')]");

    public SidebarPage clickMenu() {
        menu.click();
        return this;
    }

    public SidebarPage clickProfile() {
        profile.click();
        return this;
    }

    public SidebarPage clickAllPeople() {
        allPeople.click();
        return this;
    }
}
