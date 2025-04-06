package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.web.page.ProfilePage;

import static com.codeborne.selenide.Selenide.$;

public class Header {

    private final SelenideElement profileMenuBtn = $("button[aria-label='Menu']");
    private final SelenideElement profileMenu = $("ul[role='menu']");
    private final SelenideElement profileMenuItem = profileMenu.$("a[href='/profile']");

    public ProfilePage goToProfile() {
        profileMenuBtn.click();
        profileMenuItem.click();
        return new ProfilePage();
    }

}
