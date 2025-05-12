package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement amountInput = $("#amount");

  public EditSpendingPage editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  public MainPage editAmount(String amount){
    amountInput.clear();
    amountInput.setValue(amount);
    save();
    return new MainPage();
  }

  public EditSpendingPage save() {
    submitBtn.click();
    return this;
  }
}
