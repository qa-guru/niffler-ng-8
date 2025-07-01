package guru.qa.niffler.page;

import static com.codeborne.selenide.Selenide.page;

public class AllPages {

    protected AllPeoplePage allPeoplePage() {
        return page(AllPeoplePage.class);
    }

    protected SpendingPage spendingPage() {
        return page(SpendingPage.class);
    }

    protected FriendsPage friendsPage() {
        return page(FriendsPage.class);
    }

    protected LoginPage loginPage() {
        return page(LoginPage.class);
    }

    protected MainPage mainPage() {
        return page(MainPage.class);
    }

    protected ProfilePage profilePage() {
        return page(ProfilePage.class);
    }

    protected RegisterPage registerPage() {
        return page(RegisterPage.class);
    }

    protected SidebarPage sidebarPage() {
        return page(SidebarPage.class);
    }
}
