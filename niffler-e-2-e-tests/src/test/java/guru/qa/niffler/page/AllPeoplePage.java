package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
    private final ElementsCollection tableRows = $$("#all tr");

    @Getter
    @AllArgsConstructor
    public enum Status{
        REQUEST_SEND("Waiting..."),
        REQUEST_NOT_SEND("Add friend");
        private String buttonText;
    }


    private SelenideElement getUser(String username) {
        return  tableRows.find(text(username));
    }

    public AllPeoplePage assertUserStatus(String username, Status status){
        tableRows.find(text(username))
                .$$("td")
                .get(1)
                .$("span")
                .shouldHave(text(status.getButtonText()));
        return this;
    }
}
