package guru.qa.niffler.config;

import org.jetbrains.annotations.NotNull;

enum LocalConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5433/niffler-auth";
  }

  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8090/";
  }

  @Override
  public String userdataUrl() {
    return "http://127.0.0.1:8089/";
  }

  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5433/niffler-userdata";
  }

  @Override
  public String currencyUrl() {
    return "http://127.0.0.1:8091/";
  }

  @Override
  public String spendUrl() {
    return "http://127.0.0.1:8093/";
  }

  @Override
  public String spendJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5433/niffler-spend";
  }

  @Override
  public String currencyJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5433/niffler-currency";
  }

  @Override
  public String currencyGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String allureDockerServiceUrl() {
    return "http://127.0.0.1:5050/";
  }

  @NotNull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/local/";
  }

  @NotNull
  @Override
  public String kafkaAddress() {
    return "127.0.0.1:9092";
  }
}
