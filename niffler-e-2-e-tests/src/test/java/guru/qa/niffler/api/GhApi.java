package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GhApi {
    @GET("repos/IlyaLesnikov/niffler-ng-8/issues/{issue_number}")
    Call<JsonNode> getIssue(@Header("Authorization") String bearer, @Path("issue_number") String issueNumber);
}
