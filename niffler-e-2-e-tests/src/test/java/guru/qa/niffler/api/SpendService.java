package guru.qa.niffler.api;

public class SpendService extends ApiBaseClient {

    private final SpendServiceClient spendClient = create(SpendServiceClient.class);

    private SpendService() {
        super(CFG.spendUrl());
    }

    public static SpendServiceClient client() {
        return Holder.INSTANCE.spendClient;
    }

    private static class Holder {
        private static final SpendService INSTANCE = new SpendService();
    }

}
