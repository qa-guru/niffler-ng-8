package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.data.dataClasses.UserData;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.model.users.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.generateUser;

@ExtendWith(BrowserExtension.class)
public class UI71Test extends BaseUITest {

    @Test
    void friendRequestAcceptance() {
        //Создание юзера и связей
        UserJson maiUser = UserData.mainUser();
        UserJson newUser = generateUser();
        userClient.createUser(newUser);
        UUID newId = userClient.getUserId(newUser);
        userClient.addIncomeInvitation(newId, maiUser.id());
        //Act
        loginPage().doLogin(UserData.mainUser());
        sidebarPage().header.toFriendsPage();
        friendsPage().table.applyFriendRequest(newUser.username());
        //Assert
        friendsPage().table.checkFriendExist(newUser.username());
        //Cleanup
        friendsPage().table.unfriend(newUser.username());
        friendsPage().delete().deleteButton.click();
        userClient.deleteUser(newUser);
    }

    @Test
    void friendRequestDecline() {
        //Создание юзера и связей
        UserJson maiUser = UserData.mainUser();
        UserJson newUser = generateUser();
        userClient.createUser(newUser);
        UUID newId = userClient.getUserId(newUser);
        userClient.addIncomeInvitation(newId, maiUser.id());
        //Act
        loginPage().doLogin(UserData.mainUser());
        sidebarPage().header.toFriendsPage();
        friendsPage().table.declineFriendRequest(newUser.username());
        friendsPage().decline().declineBtn.click();
        //Assert
        friendsPage().table.checkFriendNotExist(newUser.username());
        //Cleanup
        userClient.deleteUser(newUser);
    }

    @Test
    @ApiLogin(username = "test",password = "12345")
    void addingNewSpending() {
        int amount = RandomDataUtils.getRandomNumberInRange(1, 9999);
        String description = RandomDataUtils.getRandomShortString();
        Selenide.open(CFG.frontUrl());
        sidebarPage().header.toNewSpending();
        spendingPage().amount.clearThenFill(String.valueOf(amount));
        spendingPage().category.pickRandomCategory();
        spendingPage().description.fill(description);
        spendingPage().add.click();

        mainPage().table.checkTableContainsSpendingByDescription(description);

        spendDbClient.deleteAllSpendsByUsername(UserData.mainUser().username());
    }

    @Test
    void editingProfile() {
        Faker faker = new Faker();
        loginPage().doLogin(UserData.mainUser());
        sidebarPage().header.toProfile();
        profilePage().nameInput.clearThenFill(faker.name().firstName());
        profilePage().saveChangesBtn.jsClick();
        profilePage().checkAlertMessage("Profile successfully updated");
    }
}
