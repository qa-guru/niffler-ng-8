package guru.qa.niffler.api;

import guru.qa.niffler.retrofit.TestResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthServiceClient {

    @GET("login")
    TestResponse<Void, Void> loginGet();

    @FormUrlEncoded
    @GET("login")
    TestResponse<Void, Void> loginPost(@Field("_csrf") String csrf,
                                       @Field("username") String username,
                                       @Field("password") String password);

    @GET("register")
    TestResponse<Void, Void> registerGet();

    @FormUrlEncoded
    @POST("register")
    TestResponse<Void, Void> registerPost(@Field("_csrf") String csrf,
                                          @Field("username") String username,
                                          @Field("password") String password,
                                          @Field("passwordSubmit") String passwordSubmit);


}
