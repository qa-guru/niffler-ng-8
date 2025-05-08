package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String caneName = "Niffler backend logs";
    @Override
    @SneakyThrows
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caneName));
        allureLifecycle.startTestCase(caseId);

        allureLifecycle.addAttachment(
                "Niffler-auth log",
                "text/html",
                ".log",
                Files.newInputStream(
                        Path.of("./logs/niffler_auth/app.log")
                )
        );

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }
}
