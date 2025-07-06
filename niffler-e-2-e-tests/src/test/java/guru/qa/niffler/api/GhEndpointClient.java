package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import javax.annotation.Nonnull;

public interface GhEndpointClient {

  @GET("repos/KonstantinKshnyakin/qa-guru-niffler-ng-8/issues/{issue_number}")
  @Headers({
      "Accept: application/vnd.github+json",
      "X-GitHub-Api-Version: 2022-11-28"
  })
  TestResponse<JsonNode, Void> getIssue(@Header("Authorization") String bearerToken,
                                        @Path("issue_number") String issueNumber);

  default @Nonnull String getIssueState(@Nonnull String issueNumber) {
    String githubToken = "Bearer " + System.getenv("GITHUB_TOKEN");
    System.out.println(githubToken);
    return getIssue(githubToken, issueNumber).getBody().get("state").asText();
  }

}
