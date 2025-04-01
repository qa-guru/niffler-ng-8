package guru.qa.niffler.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement saveChangesBtn = $("#save");

  public MainPage editDescription(String description) {
    setDescription(description);
    clickSaveChanges();
    return new MainPage();
  }

  public EditSpendingPage setDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  public EditSpendingPage clickSaveChanges() {
    saveChangesBtn.click();
    return this;
  }

}
