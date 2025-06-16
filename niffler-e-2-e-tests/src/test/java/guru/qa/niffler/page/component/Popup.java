package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$x;

public class Popup {

    public Popup checkSuccessEditing() {
        $x("//div[.='Profile successfully updated']").shouldBe(Condition.visible);
        return this;
    }
}
