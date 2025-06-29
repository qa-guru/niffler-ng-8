package guru.qa.niffler.config;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  INSTANCE;

  @Override
  public @Nonnull String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @Override
  public @Nonnull String spendUrl() {
    return "http://127.0.0.1:8093/";
  }

  @Override
  public @Nonnull String spendJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/niffler-spend";
  }

  @Override
  public @Nonnull String authUrl() {
    return "http://127.0.0.1:9000";
  }

  @Override
  public @Nonnull String authJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/niffler-auth";
  }

  @Override
  public @Nonnull String gatewayUrl() {
    return "http://127.0.0.1:8090";
  }

  @Override
  public @Nonnull String userdataUrl() {
    return "http://127.0.0.1:8089";
  }

  @Override
  public @Nonnull String userdataJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/niffler-userdata";
  }

  @Override
  public @Nonnull String currencyJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/niffler-currency";
  }

}
