package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.allure.ScreenDiff;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.util.IOUtil;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String CHECK_SCREENSHOT = "check screenshot";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
            parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ScreenShotTest annotation = extensionContext.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);
        return IOUtil.getBufferedImage(annotation.value());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (throwable instanceof AssertionError err) {
            if (err.getMessage().contains(CHECK_SCREENSHOT)) {
                Optional<ScreenShotTest> optionalAnno = AnnotationSupport.findAnnotation(
                    context.getRequiredTestMethod(), ScreenShotTest.class
                );
                if (optionalAnno.isPresent()) {
                    ScreenShotTest annotation = optionalAnno.get();
                    BufferedImage expected = getExpected();
                    BufferedImage actual = getActual();
                    if (annotation.rewriteExpected()) {
                        IOUtil.writeBufferedImage(actual, annotation.value());
                    } else {
                        ScreenDiff screenDiff = ScreenDiff.of(
                            expected,
                            actual,
                            getDiff()
                        );
                        Allure.addAttachment(
                            "Screenshot diff",
                            "application/vnd.allure.image.diff",
                            objectMapper.writeValueAsString(screenDiff)
                        );
                    }
                } else {
                    throw new IllegalStateException("Не найдена аннотация ScreenShotTest");
                }
            }
        }
        throw throwable;
    }

    public static void setExpected(@Nonnull BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static @Nullable BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(@Nonnull BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", expected);
    }

    public static @Nullable BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(@Nonnull BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", expected);
    }

    public static @Nullable BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

}
