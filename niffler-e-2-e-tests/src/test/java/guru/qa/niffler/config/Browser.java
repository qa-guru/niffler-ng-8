package guru.qa.niffler.config;

import com.codeborne.selenide.SelenideConfig;

public enum Browser {

    CHROME {
        public SelenideConfig getConfig() {
            return ConfigHolder.CHROME_CONFIG;
        }
    },
    FIREFOX {
        public SelenideConfig getConfig() {
            return ConfigHolder.FIREFOX_CONFIG;
        }
    };

    public abstract SelenideConfig getConfig();

    private static class ConfigHolder {
        private static final SelenideConfig CHROME_CONFIG = new SelenideConfig()
            .browser("chrome")
            .pageLoadStrategy("eager")
            .timeout(5000L);

        private static final SelenideConfig FIREFOX_CONFIG = new SelenideConfig()
            .browser("firefox")
            .pageLoadStrategy("eager")
            .timeout(5000L);
    }
}
