package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class GhApiClient {

    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().gitHubUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final GhApi ghApi = retrofit.create(GhApi.class);

    @SneakyThrows
    public String issueState(String issueNumber) {
        String token = System.getenv(GH_TOKEN_ENV);
        if (token == null) {
            throw new IllegalStateException("No GitHub token in env!");
        }

        Response<JsonNode> resp = ghApi
                .issue("token " + token, issueNumber)
                .execute();

        if (!resp.isSuccessful() || resp.body() == null) {
            System.err.println(resp.raw());
            System.err.println(resp.message());
            throw new IllegalStateException("GitHub API failed with code: " + resp.code());
        }

        return resp.body().get("state").asText();
    }
}
