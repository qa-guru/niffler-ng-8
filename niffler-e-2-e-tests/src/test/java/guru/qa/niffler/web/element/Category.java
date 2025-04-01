package guru.qa.niffler.web.element;

import com.codeborne.selenide.SelenideElement;

public class Category {

    private final SelenideElement root;

    public Category(SelenideElement root) {
        this.root = root;
    }

    public String getName() {
        return root.$(".MuiChip-label").text();
    }

    public Category clickEditBtn() {
        root.$("button[aria-label='Edit category']").click();
        return this;
    }

    public Category clickArchiveBtn() {
        root.$("button[aria-label='Archive category']").click();
        return this;
    }

    public Category clickUnarchivedBtn() {
        root.$("button[aria-label='Unarchive category']").click();
        return this;
    }

    public boolean isArchival() {
        return root.$("button[aria-label='Unarchive category']").exists();
    }

}