package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CategoryJson;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class ProfilePage extends  BasePage {

    private final SelenideElement uploadNewPictureBtn =$("span[tabindex='0']");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesBtn =$x("//button[contains(text(), 'Save changes')]");
    private final SelenideElement showArchivedTgl =$("input[class^='PrivateSwitchBase-input']");
    private final SelenideElement newCategoryInput = $("#username");
    private final ElementsCollection categoryList = $$("div.MuiBox-root.css-1lekzkb");
    private final SelenideElement editBtn =$("button[aria-label=\"Edit category\"]");
    private final SelenideElement archiveBtn =$("button[aria-label=\"Archive category\"]");



    public ProfilePage addName(String userName){
        nameInput.setValue(userName);
        return  this;
    }

    public ProfilePage addNewCategory(String categoryName){
        newCategoryInput.setValue(categoryName).pressEnter();
        return  this;
    }

    public  void assertCategoryIsShown(CategoryJson categoryJson){
       if(categoryJson.archived()){
           showArchivedTgl.click();
       }
        categoryList.find(Condition.text(categoryJson.name())).shouldBe(Condition.visible);
    }
}
