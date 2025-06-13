package guru.qa.niffler.api;

import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.*;

import java.util.List;

public interface UserdataServiceClient {

    @GET("internal/users/current")
    TestResponse<UserdataUserJson, Void> userCurrentGet(@Query("username") String username);

    @POST("internal/users/update")
    TestResponse<UserdataUserJson, Void> userUpdatePost(@Body UserdataUserJson user);

    @GET("internal/users/all")
    TestResponse<List<UserdataUserJson>, Void> userAllGet(@Query("username") String username,
                                                          @Query("searchQuery") String searchQuery);

    @GET("internal/friends/all")
    TestResponse<List<UserdataUserJson>, Void> friendAllGet(@Query("username") String username,
                                                            @Query("searchQuery") String searchQuery);

    @DELETE("internal/friends/remove")
    TestResponse<Void, Void> friendDelete(@Query("username") String username,
                                          @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/accept")
    TestResponse<UserdataUserJson, Void> invitationAcceptPost(@Query("username") String username,
                                                              @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/decline")
    TestResponse<UserdataUserJson, Void> invitationDeclinePost(@Query("username") String username,
                                                               @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/send")
    TestResponse<UserdataUserJson, Void> invitationSendPost(@Query("username") String username,
                                                            @Query("targetUsername") String targetUsername);

}
