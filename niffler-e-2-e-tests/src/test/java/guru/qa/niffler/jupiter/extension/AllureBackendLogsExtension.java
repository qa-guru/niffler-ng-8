package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

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
    public void afterSuite() {
        if(!"docker".equals(System.getProperty("test.env"))) {
            final AllureLifecycle allureLifecycle = Allure.getLifecycle();
            final String caseId = UUID.randomUUID().toString();
            allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
            allureLifecycle.startTestCase(caseId);

            for (String serviceName : serviceNames) {
                logAttachment(serviceName, allureLifecycle);
            }

            allureLifecycle.stopTestCase(caseId);
            allureLifecycle.writeTestCase(caseId);
        }
    }


    private static void logAttachment(String serviceName, AllureLifecycle allureLifecycle) {
        String name = String.format("Niffler-%s log",serviceName);
        String path = String.format("./logs/niffler_%s/app.log",serviceName);

        try {
            allureLifecycle.addAttachment(
                    name,
                    "text/html",
                    ".log",
                    Files.newInputStream(
                            Path.of(path)
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
