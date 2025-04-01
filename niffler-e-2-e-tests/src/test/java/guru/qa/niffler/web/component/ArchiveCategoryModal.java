package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ArchiveCategoryModal {

    private final SelenideElement root = $(".MuiDialog-paperScrollPaper");
    private final SelenideElement archiveBtn = root.$("button[value='Archive']");

    public void clickArchiveBtn() {
        archiveBtn.click();
    }

}