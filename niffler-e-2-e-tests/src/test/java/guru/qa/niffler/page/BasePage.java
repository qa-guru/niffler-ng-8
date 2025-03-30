package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$x;

public class BasePage {

    private final String errorBaseLocator = "//span[contains(text(), '%s')] | //p[contains(text(), '%s')]";

    public void assertErrorShown(String errorText) {
        $x(String.format(errorBaseLocator, errorText,errorText)).shouldBe(Condition.visible.because("Должна отобразиться ошибка :: " + errorText));
    }
}
