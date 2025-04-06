package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class GhApiClient {
    private static final Config CFG = Config.getInstance();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.frontUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final GhApi ghApi = retrofit.create(GhApi.class);

    public String getIssue(String issueNumber) {
        JsonNode response;
        try {
            response = ghApi.getIssue("", issueNumber)
                    .execute()
                    .body();
        } catch (IOException exception) {
            throw new AssertionError("Не удалось выполнить запрос на эндпоинт");
        }
        return response.get("id").asText();
    }
}
