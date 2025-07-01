package guru.qa.niffler.web.page;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.util.ScreeDiffResult;
import guru.qa.niffler.web.component.Alert;
import guru.qa.niffler.web.component.Modal;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.jupiter.extension.ScreenShotTestExtension.CHECK_SCREENSHOT;

@Getter
public abstract class BasePage<P extends BasePage<P>> {

    private final Modal<P> modal = new Modal<>((P) this);
    private final Alert<P> alert = new Alert<>((P) this);

    @Step("Проверяем скриншот")
    protected void checkScreenshot(BufferedImage expImage, BufferedImage actImage) {
        ScreeDiffResult booleanSupplier = new ScreeDiffResult(expImage, actImage);
        Assertions.assertFalse(booleanSupplier, CHECK_SCREENSHOT);
    }

    @Step("Обновляем страницу")
    public P refresh() {
        Selenide.refresh();
        return (P) this;
    }

    public P checkAllerIsSuccess(String expText) {
        return alert.checkAllerIsSuccess(expText)
            .returnToPage();
    }

    protected void sleepSec(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}