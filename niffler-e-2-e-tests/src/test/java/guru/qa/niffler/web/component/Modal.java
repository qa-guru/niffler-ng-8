package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Modal<P> extends BaseComponent<P, Modal<P>> {

    private final SelenideElement cancelBtn = self.$(".MuiButton-textPrimary");
    private final SelenideElement acceptBtn = self.$(".MuiButton-containedPrimary");

    public Modal(P currentPage) {
        super(currentPage, $("div.MuiDialog-paperScrollPaper"));
    }

    @Step("Кликаем на кнопку отмены действия")
    public Modal<P> clickCancelBtn() {
        cancelBtn.click();
        return this;
    }

    @Step("Кликаем на кнопку подтверждение действия")
    public Modal<P> clickAcceptBtn() {
        acceptBtn.click();
        return this;
    }
}
