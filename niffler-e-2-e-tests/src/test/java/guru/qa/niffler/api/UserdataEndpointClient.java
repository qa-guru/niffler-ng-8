package guru.qa.niffler.api;

import guru.qa.niffler.api.model.ErrorJson;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UserdataEndpointClient {

    @GET("internal/users/current")
    @Nonnull
    TestResponse<UserdataUserJson, ErrorJson> userCurrentGet(@Query("username") String username);

    @POST("internal/users/update")
    @Nonnull
    TestResponse<UserdataUserJson, ErrorJson> userUpdatePost(@Body UserdataUserJson user);

    @GET("internal/users/all")
    @Nonnull
    TestResponse<List<UserdataUserJson>, ErrorJson> userAllGet(@Query("username") String username,
                                                               @Nullable @Query("searchQuery") String searchQuery);

    @GET("internal/friends/all")
    @Nonnull
    TestResponse<List<UserdataUserJson>, ErrorJson> friendAllGet(@Query("username") String username,
                                                                 @Nullable @Query("searchQuery") String searchQuery);

    @DELETE("internal/friends/remove")
    @Nonnull
    TestResponse<Void, ErrorJson> friendDelete(@Query("username") String username,
                                          @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/accept")
    @Nonnull
    TestResponse<UserdataUserJson, ErrorJson> invitationAcceptPost(@Query("username") String username,
                                                              @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/decline")
    @Nonnull
    TestResponse<UserdataUserJson, ErrorJson> invitationDeclinePost(@Query("username") String username,
                                                               @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/send")
    @Nonnull
    TestResponse<UserdataUserJson, ErrorJson> invitationSendPost(@Query("username") String username,
                                                            @Query("targetUsername") String targetUsername);

}
