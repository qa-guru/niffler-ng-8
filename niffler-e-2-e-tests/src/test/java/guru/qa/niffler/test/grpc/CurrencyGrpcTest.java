package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.db.SpendDbClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static guru.qa.niffler.grpc.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyGrpcTest extends BaseGrpcTest {

    private final SpendClient spendClient = new SpendDbClient();

    @Test
    @DisplayName("grpc method getAllCurrencies should return all currencies and rates from db")
    void allCurrenciesShouldReturned(){
        final Set<Currency> actualList = new HashSet<>(blockingStub
                .getAllCurrencies(Empty.getDefaultInstance())
                .getAllCurrenciesList()
        );

        final Set<Currency> expectedList = new HashSet<>(spendClient
                .getAllCurrencies()
        );

        assertEquals(
                actualList,
                expectedList,
                String.format(
                        "List mismatch: \n actual:%s \nexpected: %s",
                        actualList,
                        expectedList
                )
        );
    }


    static Stream<Arguments> spendCurrencyShouldBeConverted() {
        return Stream.of(
                Arguments.of(150.00, RUB, KZT),
                Arguments.of(34.00, USD, EUR),
                Arguments.of(150.00, RUB, RUB),
                Arguments.of(0.00, KZT, RUB)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "grpc method calculateRate should return exchanged to {2} amount from {0} {1}")
    void spendCurrencyShouldBeConverted(double spend,
                                        guru.qa.niffler.grpc.CurrencyValues spendCurrency,
                                        guru.qa.niffler.grpc.CurrencyValues desiredCurrency) {
        final var calculateRequest = CalculateRequest.newBuilder()
                .setAmount(spend)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final double actualResult = blockingStub.calculateRate(calculateRequest)
                .getCalculatedAmount();
        final double expectedResult = spendClient.exchange(spend,spendCurrency,desiredCurrency);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("grpc method calculateRate should return error in case when spend amount less than 0")
    void negativeSpendAmountShouldInvokeError(){
        final var calculateRequest = CalculateRequest.newBuilder()
                .setAmount(-5)
                .setSpendCurrency(EUR)
                .setDesiredCurrency(USD)
                .build();
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> blockingStub.calculateRate(calculateRequest)
        );
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
    }
}
