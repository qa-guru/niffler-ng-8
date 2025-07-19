package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.retrofit.TestResponse;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractApiClient {

    protected <SUCC_DTO, ERR_DTO> void validateSuccess(TestResponse<SUCC_DTO, ERR_DTO> response) {
        if (!response.isSuccessful()) {
            throwIllegalStateException(response);
        }
    }

    protected <SUCC_DTO, ERR_DTO> @Nonnull SUCC_DTO validateSuccessAndGetBody(TestResponse<SUCC_DTO, ERR_DTO> response) {
        SUCC_DTO body = response.getBody();
        if (response.isSuccessful() && body != null) {
            return body;
        } else {
            return throwIllegalStateException(response);
        }
    }

    protected <SUCC_DTO extends Collection<B>, ERR_DTO, R, B> @Nonnull List<R> validateSuccessAndMapList(
        TestResponse<SUCC_DTO, ERR_DTO> response,
        Function<B, R> function
    ) {
        SUCC_DTO body = response.getBody();
        if (response.isSuccessful() && body != null) {
            return body.stream()
                .map(function)
                .collect(Collectors.toList());
        } else {
            return throwIllegalStateException(response);
        }
    }

    @SneakyThrows
    protected <SUCC_DTO, ERR_DTO, R> R throwIllegalStateException(TestResponse<SUCC_DTO, ERR_DTO> response) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Запрос выполнился некорректно:");
        sj.add(response.getRetrofitRawResponse().toString());
        ERR_DTO errorBody = response.getErrorBody();
        if (Objects.nonNull(errorBody)) {
            sj.add(errorBody.toString());
        }
        throw new IllegalStateException(sj.toString());
    }
}
