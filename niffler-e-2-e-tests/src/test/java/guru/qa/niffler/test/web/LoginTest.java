package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.category.Category;
import guru.qa.niffler.jupiter.spending.Spend;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {
    private static final Config CFG = Config.getInstance();
    String actualLogin = CFG.mainUserLogin();
    String actualPass = CFG.mainUserPass();


    @Spend(
            username = "duck",
            category = "Обучение",
            description = "Обучение Niffler 2.0",
            amount = 89000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("duck", "12345")
                .editSpending(spend.description())
                .editDescription(newDescription);

        new MainPage().checkThatTableContains(newDescription);
    }

    @Test
    void shouldRegisterNewUser() {
        String userName = new Faker().name().username();
        String pass = new Faker().number().digits(5);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccount();
        new RegisterPage().setUsername(userName)
                .setPassword(pass)
                .setPasswordSubmit(pass)
                .signUp().signIn();

        new LoginPage().doLogin(actualLogin, actualPass);


        new SidebarPage().clickMenu()
                .clickAllPeople();
        new AllPeoplePage().findPersonByName(userName)
                .checkTableSize(1)
                .checkTableContainsPerson(userName);

    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccount();
        new RegisterPage().setUsername(actualLogin)
                .setPassword(actualPass)
                .setPasswordSubmit(actualPass)
                .signUp().checkError("Username `" + actualLogin + "` already exists");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String userName = new Faker().name().username();
        String pass = new Faker().number().digits(5);
        String wrongPass = new Faker().number().digits(5);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickCreateNewAccount();
        new RegisterPage().setUsername(userName)
                .setPassword(pass)
                .setPasswordSubmit(wrongPass)
                .signUp().checkError("Passwords should be equal");
    }

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        new MainPage().checkTableVisible();
        Assertions.assertEquals(CFG.frontUrl() + "main", WebDriverRunner.url());
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String wrongPass = new Faker().number().digits(5);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, wrongPass);
        new MainPage().checkTableNotVisible();
        new LoginPage().checkError("Неверные учетные данные пользователя");
    }


    @Category(
            username = "test",
            archived = true
    )
    @Test
    void archivedCategoryCantBePresentedInCategoryList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);

        new SidebarPage().clickMenu().clickProfile();
        new ProfilePage().hideArchive()
                .checkCategoryNotExistInTable(category.name());
    }


    @Category(
            username = "test",
            archived = false
    )
    @Test
    void activeCategoryShouldPresentInCategoryList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(actualLogin, actualPass);
        new SidebarPage().clickMenu().clickProfile();
        new ProfilePage().hideArchive()
                .checkTableContainsCategory(category.name());
    }
}
