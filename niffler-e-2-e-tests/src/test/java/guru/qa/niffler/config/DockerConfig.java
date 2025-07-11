package guru.qa.niffler.config;

enum DockerConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
  }

  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5433/niffler-auth";
  }

  @Override
  public String gatewayUrl() {
    return "http://auth.niffler.dc:8090/";
  }

  @Override
  public String userdataUrl() {
    return "http://auth.niffler.dc:8089/";
  }

  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5433/niffler-userdata";
  }

  @Override
  public String currencyUrl() {
    return "http://auth.niffler.dc:8091/";
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
    return "auth.niffler.dc";
  }
}
