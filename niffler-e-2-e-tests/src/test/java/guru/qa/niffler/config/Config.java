package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
      ? DockerConfig.INSTANCE
      : LocalConfig.INSTANCE;
  }

  @Nonnull
  String allureDockerServiceUrl();

  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String gatewayUrl();

  String userdataUrl();

  String userdataJdbcUrl();

  String currencyUrl();

  String spendUrl();

  String spendJdbcUrl();

  String currencyJdbcUrl();

  default String ghUrl(){
    return "https://api.github.com/";
  }

  @Nonnull
  default String defaultPassword(){
    return "12345";
  }

  String currencyGrpcAddress();

  default int currencyGrpcPort() {
    return 8092;
  }

  @Nonnull
  String screenshotBaseDir();
}
