package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement sumInput = $("#amount");

  public MainPage editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
    return new MainPage();
  }

  public MainPage editSum(String sum) {
    sumInput.clear();
    sumInput.setValue(sum);
    submitBtn.click();
    return new MainPage();
  }

  public MainPage editSum(UserJson user) {
    for(SpendJson spend :user.testData().spendings()){
      String newSum = String.valueOf((int)(spend.amount()+25));
      editSum(newSum);
    }
    return new MainPage();
  }
}
