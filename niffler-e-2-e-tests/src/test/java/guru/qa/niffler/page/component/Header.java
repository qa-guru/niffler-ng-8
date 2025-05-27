package guru.qa.niffler.page.component;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

  public Header() {
    super($("#root header"));
  }

  public void checkHeaderText() {
    self.$("h1").shouldHave(text("Niffler"));
  }
}
