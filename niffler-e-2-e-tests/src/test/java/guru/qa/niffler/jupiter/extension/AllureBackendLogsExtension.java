package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String caseName = "Niffler backend logs";
    private static final Set<String> serviceNames = Set.of(
            "auth",
            "userdata",
            "spend",
            "gateway",
            "currency"
    );
    @Override
    @SneakyThrows
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        for(String serviceName : serviceNames){
            logAttachment(serviceName,allureLifecycle);
        }

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }


    private static void logAttachment(String serviceName, AllureLifecycle allureLifecycle) throws IOException {
        String name = String.format("Niffler-%s log",serviceName);
        String path = String.format("./logs/niffler_%s/app.log",serviceName);

        allureLifecycle.addAttachment(
                name,
                "text/html",
                ".log",
                Files.newInputStream(
                        Path.of(path)
                )
        );
    }
}
