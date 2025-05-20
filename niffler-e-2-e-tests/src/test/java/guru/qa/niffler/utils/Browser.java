package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;

public enum Browser {
    CHROME, FIREFOX;

    public SelenideConfig config(){
        return new SelenideConfig()
                .browser(this.name().toLowerCase())
                .pageLoadStrategy("eager")
                .timeout(5000L);
    }
}
