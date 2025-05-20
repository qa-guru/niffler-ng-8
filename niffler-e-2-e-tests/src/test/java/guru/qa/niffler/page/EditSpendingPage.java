package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

public class EditSpendingPage extends BasePage {

  private final SelenideElement descriptionInput;
  private final SelenideElement submitBtn;
  private final SelenideElement sumInput;

  public EditSpendingPage(SelenideDriver driver){
    super(driver);
    this.descriptionInput = $("#description");
    this.submitBtn = $("#save");
    this.sumInput = $("#amount");
  }

  public EditSpendingPage(){
    this(null);
  }

  public MainPage editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
    return new MainPage(driver);
  }

  public MainPage editSum(String sum) {
    sumInput.clear();
    sumInput.setValue(sum);
    submitBtn.click();
    return new MainPage(driver);
  }
}
