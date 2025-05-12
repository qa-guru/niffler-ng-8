package guru.qa.niffler.api;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("/register")
    Call<Void> register(@Query("_csrf") String csrf,
                        @Query("username") String username,
                        @Query("password") String password,
                        @Query("passwordSubmit") String passwordSubmit);
}
