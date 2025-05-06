package guru.qa.niffler.utils;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuccessRequestExecutor {

    public  <T> T executeRequest(Call<T> call) {
        try {
            Response<T> response = call.execute();
            assertEquals(2, response.code()/100);
            return response.body();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
