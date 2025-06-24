package guru.qa.niffler.retrofit;


import lombok.Value;
import okhttp3.Headers;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value
public class TestResponse<SUCC_DTO, ERR_DTO> {

    @Nonnull
    Response<SUCC_DTO> retrofitRawResponse;
    @Nullable
    SUCC_DTO body;
    @Nullable
    ERR_DTO errorBody;

    private TestResponse(@Nonnull Response<SUCC_DTO> retrofitRawResponse, @Nullable Object errorBody) {
        this.retrofitRawResponse = retrofitRawResponse;
        this.body = retrofitRawResponse.body();
        this.errorBody = (ERR_DTO) errorBody;
    }

    public @Nonnull String getMessage() {
        return retrofitRawResponse.message();
    }

    public @Nonnull Headers getHeaders() {
        return retrofitRawResponse.headers();
    }

    public int getCode() {
        return retrofitRawResponse.code();
    }

    public boolean isSuccessful() {
        return retrofitRawResponse.isSuccessful();
    }

    public @Nonnull okhttp3.Response getOkhttpRawResponse() {
        return retrofitRawResponse.raw();
    }

    public static <SUCC_DTO> TestResponse<SUCC_DTO, Object> of(Response<SUCC_DTO> response, Object errorDto) {
        return new TestResponse<>(response, errorDto);
    }

    @Override
    public String toString() {
        return "TestResponse{" +
            "body=" + body +
            ", errorBody=" + errorBody +
            ", retrofitRawResponse=" + retrofitRawResponse +
            ", message=" + retrofitRawResponse.message() +
            ", headers=" + retrofitRawResponse.headers() +
            '}';
    }
}
