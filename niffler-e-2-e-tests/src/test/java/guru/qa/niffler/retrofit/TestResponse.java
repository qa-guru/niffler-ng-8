package guru.qa.niffler.retrofit;


import lombok.Value;
import okhttp3.Headers;
import retrofit2.Response;

@Value
public class TestResponse<SUCC_DTO, ERR_DTO> {

    Response<SUCC_DTO> retrofitRawResponse;
    SUCC_DTO body;
    ERR_DTO errorBody;

    private TestResponse(Response<SUCC_DTO> retrofitRawResponse, Object errorBody) {
        this.retrofitRawResponse = retrofitRawResponse;
        this.body = retrofitRawResponse.body();
        this.errorBody = (ERR_DTO) errorBody;
    }

    public String getMessage() {
        return retrofitRawResponse.message();
    }

    public Headers getHeaders() {
        return retrofitRawResponse.headers();
    }

    public int getCode() {
        return retrofitRawResponse.code();
    }

    public boolean isSuccessful() {
        return retrofitRawResponse.isSuccessful();
    }

    public okhttp3.Response getOkhttpRawResponse() {
        return retrofitRawResponse.raw();
    }

    public static <SUCC_DTO> TestResponse<SUCC_DTO, Object> of(Response<SUCC_DTO> response, Object errorDto) {
        return new TestResponse<>(response, errorDto);
    }

}
