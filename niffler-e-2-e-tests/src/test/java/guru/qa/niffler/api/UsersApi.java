package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.UUID;

public interface UsersApi {

    @POST("internal/users/add")
    Call<UserJson> addUser(@Body UserJson user);

    @PATCH("internal/users/update")
    Call<UserJson> updateUser(@Body UserJson user);

    @GET("internal/users/{id}")
    Call<UserJson> getUser(@Path("id") UUID id);

    @GET("internal/users/all")
    Call<List<UserJson>> allUsers();

    @POST("internal/users/{userId}/friends/add/{friendId}")
    Call<Void> addFriend(
            @Path("userId") UUID userId,
            @Path("friendId") UUID friendId
    );

    @POST("internal/users/{userId}/friends/remove/{friendId}")
    Call<Void> removeFriend(
            @Path("userId") UUID userId,
            @Path("friendId") UUID friendId
    );

    @POST("internal/users/{userId}/invitations/send/{inviteeId}")
    Call<Void> sendInvitation(
            @Path("userId") UUID userId,
            @Path("inviteeId") UUID inviteeId
    );

    @POST("internal/users/{userId}/invitations/accept/{inviteId}")
    Call<Void> acceptInvitation(
            @Path("userId") UUID userId,
            @Path("inviteId") UUID inviteId
    );

    @POST("internal/users/{userId}/invitations/decline/{inviteId}")
    Call<Void> declineInvitation(
            @Path("userId") UUID userId,
            @Path("inviteId") UUID inviteId
    );
}
