package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Calendar extends BaseComponent<Calendar> {

  public Calendar(SelenideElement self) {
    super(self);
  }

  public Calendar() {
    super($(".MuiPickersLayout-root"));
  }
}
