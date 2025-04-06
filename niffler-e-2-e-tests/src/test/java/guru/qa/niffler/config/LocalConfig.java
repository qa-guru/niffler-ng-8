package guru.qa.niffler.config;

enum LocalConfig implements Config {
  instance;

  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
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
  public String currencyUrl() {
    return "http://auth.niffler.dc:8091/";
  }

  @Override
  public String spendUrl() {
    return "http://127.0.0.1:8093/";
  }

  @Override
  public String ghUrl() {
    return "https://api.github.com/";
  }
}
