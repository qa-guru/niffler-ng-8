package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Alert<P> extends BaseComponent<P> {

    private final SelenideElement cancelBtn = self.$(".MuiButton-textPrimary");
    private final SelenideElement acceptBtn = self.$(".MuiButton-containedPrimary");

    public Alert(P currentPage) {
        super(currentPage, $("div.MuiDialog-paperScrollPaper"));
    }

    @Step("Кликаем на кнопку отмены действия")
    public Alert<P> clickCancelBtn() {
        cancelBtn.click();
        return this;
    }

    @Step("Кликаем на кнопку подтверждение действия")
    public Alert<P> clickAcceptBtn() {
        acceptBtn.click();
        return this;
    }
}
