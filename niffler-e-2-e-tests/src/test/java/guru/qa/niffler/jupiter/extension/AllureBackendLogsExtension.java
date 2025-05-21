package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String caseName = "Niffler backend logs";
    private final List<String> services = List.of(
            "niffler-auth",
            "niffler-currency",
            "niffler-gateway",
            "niffler-spend",
            "niffler-userdata"
    );

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        services.forEach(service -> {
                    try {
                        allureLifecycle.addAttachment(
                                service + " log",
                                "text/html",
                                ".log",
                                Files.newInputStream(
                                        Path.of("./logs/" + service + "/app.log")
                                )
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to attach log for service " + service, e);
                    }
                }
        );

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }
}
