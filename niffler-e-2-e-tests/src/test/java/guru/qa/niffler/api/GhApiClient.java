package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Objects;

public class GhApiClient {
    private static final String GUTHUB_TOKEN_ENV = "GITHUB_TOKEN";
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().frontUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final GhApi ghApi = retrofit.create(GhApi.class);

    public String getIssueState(String issueNumber) {
        JsonNode response;
        try {
            response = ghApi.getIssue("Bearer " + System.getenv(GUTHUB_TOKEN_ENV), issueNumber)
                    .execute()
                    .body();
        } catch (IOException exception) {
            throw new AssertionError("Не удалось выполнить запрос на эндпоинт");
        }
        return Objects.requireNonNull(response).get("state").asText();
    }
}
