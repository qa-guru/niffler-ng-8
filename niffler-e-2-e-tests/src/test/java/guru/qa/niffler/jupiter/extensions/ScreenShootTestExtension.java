package guru.qa.niffler.jupiter.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotations.ScreenShootTest;
import guru.qa.niffler.model.allure.ScreenDif;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ScreenShootTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShootTestExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShootTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ScreenShootTest screenShotTest = extensionContext.getRequiredTestMethod().getAnnotation(ScreenShootTest.class);
        return ImageIO.read(new ClassPathResource(screenShotTest.value()).getInputStream());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ScreenShootTest screenShotTest = context.getRequiredTestMethod().getAnnotation(ScreenShootTest.class);
        if (screenShotTest.rewriteExpected()) {
            BufferedImage actual = getActual();
            if (actual != null) {
                ImageIO.write(actual, "png", new File("src/test/resources/" + screenShotTest.value()));
            }
        }
        ScreenDif screenDif = new ScreenDif(
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getExpected())),
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getActual())),
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getActual())));


        Allure.addAttachment("ScreenDif"
                , "application/vnd.allure.image.diff",
                objectMapper.writeValueAsString(screenDif));

        throw throwable;
    }

    public static void setExpected(BufferedImage expected) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getActual() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    public static BufferedImage getDiff() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("expdiffected", BufferedImage.class);
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
