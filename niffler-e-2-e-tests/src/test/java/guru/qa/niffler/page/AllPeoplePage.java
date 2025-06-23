package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;

@ParametersAreNonnullByDefault
public class AllPeoplePage extends BasePage<AllPeoplePage> {
    private final ElementsCollection tableRows = $$("#all tr");

    @Override
    public String getUrl() {
        return "people/all";
    }

    @Getter
    private final SearchField searchField = new SearchField(driver);

    @Getter
    @AllArgsConstructor
    public enum Status{
        REQUEST_SEND("Waiting..."),
        REQUEST_NOT_SEND("Add friend");
        private final String buttonText;
    }

    public AllPeoplePage(@Nullable SelenideDriver driver){
        super(driver);
    }

    public AllPeoplePage(){
        super();
    }


    @Step("assert that user {username} status is {status}")
    public AllPeoplePage assertUserStatus(String username, Status status){
        tableRows.find(text(username))
                .$$("td")
                .get(1)
                .$("span")
                .shouldHave(text(status.getButtonText()));
        return this;
    }

    @Step("assert outcome invitations for user {user}")
    public AllPeoplePage assertOutcomeInvitations(UserJson user){
        for (String username : user
                .testData()
                .outcomeInvitations()
                .stream()
                .map(UserJson::username)
                .toArray(String[]::new)){
            assertUserStatus(username,Status.REQUEST_SEND);
        }
        return this;
    }
}
