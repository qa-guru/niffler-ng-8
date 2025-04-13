package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {
  private static final String username = "NiceGuy";
  private static final String password = "qwer";

  @Category(
    username = username,
    archived = true
  )
  @Test
  void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(username, password)
      .openProfile()
      .clickShowArchivedToggle()
      .verifyCategoryPresentInList(category.name());
  }

  @Category(
    username = username,
    archived = false
  )
  @Test
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(LoginPage.URL, LoginPage.class)
      .doLogin(username, password)
      .openProfile()
      .verifyCategoryPresentInList(category.name());
  }
}