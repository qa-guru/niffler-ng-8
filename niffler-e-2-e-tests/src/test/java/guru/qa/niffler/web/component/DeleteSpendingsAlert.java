package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DeleteSpendingsAlert {

    private final SelenideElement deleteBtn = $(".MuiDialog-paperScrollPaper .MuiButton-textPrimary");
    private final SelenideElement cancelBtn = $(".MuiDialog-paperScrollPaper .MuiButton-containedPrimary");

    public DeleteSpendingsAlert clickCancelBtn() {
        deleteBtn.click();
        return this;
    }

    public DeleteSpendingsAlert clickDeleteBtn() {
        cancelBtn.click();
        return this;
    }
}
