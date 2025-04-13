package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {

  public static final String URL = Config.getInstance().frontUrl() + "/people/all";

  private final SelenideElement allPeopleTable = $("#all");

  public AllPeoplePage verifyOutcomeInvitationIsPresent(String userName) {
    allPeopleTable.$$("tr").findBy(text(userName)).shouldBe(visible);
    return this;
  }
}