package guru.qa.niffler.api;

public class GhService extends ApiBaseClient {

  private final GhServiceClient ghClient = create(GhServiceClient.class);

  private GhService() {
    super(CFG.ghUrl());
  }

  public static GhServiceClient client() {
    return Holder.INSTANCE.ghClient;
  }

  private static class Holder {
    private static final GhService INSTANCE = new GhService();
  }

}
