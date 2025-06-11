package guru.qa.niffler.utils;

import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuccessRequestExecutor {

    @Nullable
    public <T> T executeRequest(@Nonnull Call<?>... calls) {
        try {
            Response<?> response = null;
            for(Call<?> call : calls) {
                response = call.execute();
                assertTrue(response.isSuccessful());
            }
            @SuppressWarnings("unchecked")
            T result = (T) requireNonNull(response).body();
            return result;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
