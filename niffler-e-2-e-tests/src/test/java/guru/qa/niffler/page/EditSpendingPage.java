package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement amountInput = $("#amount");

  @Nonnull
  public EditSpendingPage editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }
    @Nonnull
  public MainPage editAmount(String amount){
    amountInput.clear();
    amountInput.setValue(amount);
    save();
    return new MainPage();
  }

    @Nonnull
  public EditSpendingPage save() {
    submitBtn.click();
    return this;
  }
}
