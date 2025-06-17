package guru.qa.niffler.config;

import com.codeborne.selenide.SelenideConfig;

public enum Browser {

    CHROME {
        public SelenideConfig getConfig() {
            return new SelenideConfig()
                .browser("chrome")
                .pageLoadStrategy("eager")
                .timeout(5000L);
        }
    },
    FIREFOX {
        public SelenideConfig getConfig() {
            return new SelenideConfig()
                .browser("firefox")
                .pageLoadStrategy("eager")
                .timeout(5000L);
        }
    };

    public abstract SelenideConfig getConfig();
}
