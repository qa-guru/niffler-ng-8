package guru.qa.niffler.steps;

import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Assertions;

import java.awt.image.BufferedImage;

public class AssertionSteps {

    public static void assertScreenshotsEquals(BufferedImage expected, BufferedImage actual) {
        Assertions.assertFalse(new ScreenDiffResult(expected, actual));
    }
}