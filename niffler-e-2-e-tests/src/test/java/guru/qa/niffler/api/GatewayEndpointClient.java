package guru.qa.niffler.api;

import guru.qa.niffler.api.model.*;
import guru.qa.niffler.retrofit.TestResponse;
import guru.qa.niffler.web.model.DataFilterValues;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface GatewayEndpointClient {

    @GET("api/categories/all")
    TestResponse<List<CategoryJson>, Void> allCategories(@Header("Authorization") String bearerToken);

    @POST("api/categories/add")
    TestResponse<CategoryJson, Void> addCategory(@Header("Authorization") String bearerToken,
                                                 @Body CategoryJson category);

    @GET("api/currencies/all")
    TestResponse<List<CurrencyJson>, Void> allCurrencies(@Header("Authorization") String bearerToken);

    @GET("api/friends/all")
    TestResponse<List<UserdataUserJson>, Void> allFriends(@Header("Authorization") String bearerToken,
                                                          @Nullable @Query("searchQuery") String searchQuery);

    @DELETE("api/friends/remove")
    TestResponse<Void, Void> removeFriend(@Header("Authorization") String bearerToken,
                                          @Nonnull @Query("username") String targetUsername);

    @POST("api/invitations/send")
    TestResponse<UserdataUserJson, Void> sendInvitation(@Header("Authorization") String bearerToken,
                                                        @Body FriendJson friend);

    @POST("api/invitations/accept")
    TestResponse<UserdataUserJson, Void> acceptInvitation(@Header("Authorization") String bearerToken,
                                                          @Body FriendJson friend);

    @POST("api/invitations/decline")
    TestResponse<UserdataUserJson, Void> declineInvitation(@Header("Authorization") String bearerToken,
                                                           @Body FriendJson friend);

    @GET("api/session/current")
    TestResponse<SessionJson, Void> currentSession(@Header("Authorization") String bearerToken);

    @GET("api/spends/all")
    TestResponse<List<SpendJson>, Void> allSpends(@Header("Authorization") String bearerToken,
                                                  @Nullable @Query("filterCurrency") CurrencyValues filterCurrency,
                                                  @Nullable @Query("filterPeriod") DataFilterValues filterPeriod);

    @POST("api/spends/add")
    TestResponse<SpendJson, Void> addSpend(@Header("Authorization") String bearerToken,
                                           @Body SpendJson spend);

    @PATCH("api/spends/edit")
    TestResponse<SpendJson, Void> editSpend(@Header("Authorization") String bearerToken,
                                            @Body SpendJson spend);

    @DELETE("api/spends/remove")
    TestResponse<Void, Void> removeSpends(@Header("Authorization") String bearerToken,
                                          @Nonnull @Query("ids") List<String> ids);

    @GET("api/stat/total")
    TestResponse<List<StatisticJson>, Void> totalStat(@Header("Authorization") String bearerToken,
                                                      @Nullable @Query("filterCurrency") CurrencyValues filterCurrency,
                                                      @Nullable @Query("filterPeriod") DataFilterValues filterPeriod);

    @GET("api/users/current")
    TestResponse<UserdataUserJson, Void> currentUser(@Header("Authorization") String bearerToken);

    @GET("api/users/all")
    TestResponse<List<UserdataUserJson>, Void> allUsers(@Header("Authorization") String bearerToken,
                                                        @Query("searchQuery") @Nullable String searchQuery);

    @POST("api/users/update")
    TestResponse<UserdataUserJson, Void> updateUser(@Header("Authorization") String bearerToken,
                                                    @Body UserdataUserJson user);
}
