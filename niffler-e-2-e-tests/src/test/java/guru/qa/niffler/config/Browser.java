package guru.qa.niffler.config;

import com.codeborne.selenide.SelenideConfig;
import lombok.Getter;

@Getter
public enum Browser {
    CHROME, FIREFOX;

    public SelenideConfig config() {
        return new SelenideConfig()
                .browser(this.name().toLowerCase())
                .pageLoadStrategy("eager")
                .timeout(5000L);
    }

}
