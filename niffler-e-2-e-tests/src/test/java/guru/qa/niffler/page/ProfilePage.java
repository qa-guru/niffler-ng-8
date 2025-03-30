package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.collections.AnyMatch;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {

  private final ElementsCollection categoryLabels = $$(".MuiChip-label");
  private final SelenideElement archiveCategoryBtn = $("button[aria-label='Archive category']");
  private final SelenideElement archiveCategoryDialog = $("div[role='dialog']");
  private final SelenideElement dialogDescription = $("#alert-dialog-slide-description");
  private final SelenideElement dialogArchiveBtn = $x("//button[.='Archive']");
  private final SelenideElement dialogUnArchiveBtn = $x("//button[.='Unarchive']");
  private final SelenideElement archiveCategoryAlert = $("div[role='alert']");

  private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
  private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
  private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");


  public ProfilePage categoriesShouldHaveLabel(String categoryName) {
    categoryLabels.shouldHave(new AnyMatch(
        "Category with name %s is presented".formatted(categoryName),
        category -> category.getText().equals(categoryName)
    ));
    return this;
  }

  public ProfilePage archiveCategory(String category) {
    archiveCategoryBtn.click();
    archiveCategoryDialog.should(visible);
    dialogDescription.shouldHave(text("Do you really want to archive " + category + "? After this change it won't be available while creating spends"));
    dialogArchiveBtn.click();
    archiveCategoryAlert.should(visible);
    archiveCategoryAlert.shouldHave(text("Category " + category + " is archived"));
    return this;
  }


  public ProfilePage showArchivedCategories() {
    archivedSwitcher.click();
    return this;
  }

  public ProfilePage unArchiveCategory(String category) {
    bubblesArchived.find(text(category)).parent().$("button[aria-label='Unarchive category']").click();
    archiveCategoryDialog.should(visible);
    dialogDescription.shouldHave(text("Do you really want to unarchive category " + category + "?"));
    dialogUnArchiveBtn.click();
    archiveCategoryAlert.should(visible);
    archiveCategoryAlert.shouldHave(text("Category " + category + " is unarchived"));
    return this;
  }




  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }
}
