package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");

  public void editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
  }
}
