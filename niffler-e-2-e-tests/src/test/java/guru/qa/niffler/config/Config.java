package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        return  "docker".equals(System.getProperties().get("test.env"))
                ? DockerConfig.instance : LocalConfig.instance;
    }

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
