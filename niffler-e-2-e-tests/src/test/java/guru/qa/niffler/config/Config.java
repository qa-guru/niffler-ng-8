package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        return LocalConfig.instance;
    }

    String kiwiPngPath();
    String monkeyPngPath();

    String frontUrl();

    String spendUrl();

    String spendJdbcUrl();

    String mainUserLogin();

    String mainUserPass();

    String authUrl();

    String authJdbcUrl();

    String gatewayUrl();

    String userdataUrl();

    String userdataJdbcUrl();

    String currencyJdbcUrl();

    String ghUrl();

}
