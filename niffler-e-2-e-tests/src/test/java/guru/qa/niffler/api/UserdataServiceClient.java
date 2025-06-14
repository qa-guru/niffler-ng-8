package guru.qa.niffler.api;

import guru.qa.niffler.api.model.ErrorJson;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.*;

import java.util.List;

public interface UserdataServiceClient {

    @GET("internal/users/current")
    TestResponse<UserdataUserJson, ErrorJson> userCurrentGet(@Query("username") String username);

    @POST("internal/users/update")
    TestResponse<UserdataUserJson, ErrorJson> userUpdatePost(@Body UserdataUserJson user);

    @GET("internal/users/all")
    TestResponse<List<UserdataUserJson>, ErrorJson> userAllGet(@Query("username") String username,
                                                               @Query("searchQuery") String searchQuery);

    @GET("internal/friends/all")
    TestResponse<List<UserdataUserJson>, ErrorJson> friendAllGet(@Query("username") String username,
                                                            @Query("searchQuery") String searchQuery);

    @DELETE("internal/friends/remove")
    TestResponse<Void, ErrorJson> friendDelete(@Query("username") String username,
                                          @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/accept")
    TestResponse<UserdataUserJson, ErrorJson> invitationAcceptPost(@Query("username") String username,
                                                              @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/decline")
    TestResponse<UserdataUserJson, ErrorJson> invitationDeclinePost(@Query("username") String username,
                                                               @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/send")
    TestResponse<UserdataUserJson, ErrorJson> invitationSendPost(@Query("username") String username,
                                                            @Query("targetUsername") String targetUsername);

}
