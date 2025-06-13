package guru.qa.niffler.web.page;

import guru.qa.niffler.util.ScreeDiffResult;
import org.junit.jupiter.api.Assertions;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.jupiter.extension.ScreenShotTestExtension.CHECK_SCREENSHOT;

public abstract class BasePage {

    protected void checkScreenshot(BufferedImage expImage, BufferedImage actImage) {
        ScreeDiffResult booleanSupplier = new ScreeDiffResult(expImage, actImage);
        Assertions.assertFalse(booleanSupplier, CHECK_SCREENSHOT);
    }

    protected void sleepSec(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}