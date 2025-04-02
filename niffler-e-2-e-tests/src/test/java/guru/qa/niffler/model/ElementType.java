package guru.qa.niffler.model;

import com.codeborne.selenide.WebElementCondition;

import static com.codeborne.selenide.Condition.attribute;

public enum ElementType {
    PASSWORD,
    TEXT,
    BUTTON;
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public WebElementCondition assertType(){
        return attribute("type", this.toString());
    }
}
