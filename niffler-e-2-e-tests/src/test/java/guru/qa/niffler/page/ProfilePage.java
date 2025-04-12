package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {

  private final SelenideElement avatarImage = $("main img.MuiAvatar-img");
  private final SelenideElement uploadNewPictureButton = $("label.image__input-label span[role='button']");
  private final SelenideElement userNameInput = $("input[id='username']");
  private final SelenideElement nameInput = $("input[id='name']");
  private final SelenideElement saveChangesButton = $("button[id=':r7:']");
  private final SelenideElement showArchivedToggle = $("input[type='checkbox']");
  private final SelenideElement addNewCategoryInput = $("input[id='category']");
  private final ElementsCollection categoriesList = $$("span.MuiChip-label");

  public ProfilePage clickShowArchivedToggle() {
    showArchivedToggle.click();
    return this;
  }

  public ProfilePage verifyCategoryPresentInList(String category) {
    categoriesList.find(text(category)).shouldBe(visible);
    return this;
  }
}
