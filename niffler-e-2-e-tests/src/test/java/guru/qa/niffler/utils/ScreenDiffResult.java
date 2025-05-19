package guru.qa.niffler.utils;

import guru.qa.niffler.jupiter.extensions.ScreenShootTestExtension;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

public class ScreenDiffResult implements BooleanSupplier {

    private final BufferedImage expected;
    private final BufferedImage actual;
    private final ImageDiff diff;
    private final boolean hasDff;

    public ScreenDiffResult(BufferedImage expected, BufferedImage actual) {
        this.expected = expected;
        this.actual = actual;
        this.diff =  new ImageDiffer().makeDiff(expected, actual);
        this.hasDff = diff.hasDiff();
    }

    @Override
    public boolean getAsBoolean() {
        if (hasDff) {
            ScreenShootTestExtension.setExpected(expected);
            ScreenShootTestExtension.setActual(actual);
            ScreenShootTestExtension.setDiff(diff.getMarkedImage());
        }
        return hasDff;
    }
}
