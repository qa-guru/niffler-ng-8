package guru.qa.niffler.web.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Alert<P> extends BaseComponent<P, Alert<P>> {

    private final SelenideElement message = self.$(".MuiAlert-message");

    public Alert(P currentPage) {
        super(currentPage, $("div[role='alert']"));
    }

    @Step("Проверяем что алерт успешный")
    public Alert<P> checkAllerIsSuccess(String expText) {
        checkText(expText);
        self.shouldHave(cssClass("MuiAlert-colorSuccess"));
        return this;
    }

    @Step("Проверяем текст алерта")
    public Alert<P> checkText(String expText) {
        message.shouldHave(text(expText));
        return this;
    }
}
