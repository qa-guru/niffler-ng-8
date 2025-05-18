package guru.qa.niffler.util;

import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

public class ScreeDiffResult implements BooleanSupplier {

    public final BufferedImage expected;
    public final BufferedImage actual;
    public final ImageDiff diff;
    public final boolean hasDif;

    public ScreeDiffResult(BufferedImage actual, BufferedImage expected) {
        this.actual = actual;
        this.expected = expected;
        this.diff = new ImageDiffer().makeDiff(expected, actual);
        this.hasDif = diff.hasDiff();
    }

    @Override
    public boolean getAsBoolean() {
        if (hasDif) {
            ScreenShotTestExtension.setExpected(expected);
            ScreenShotTestExtension.setActual(actual);
            ScreenShotTestExtension.setDiff(diff.getMarkedImage());
        }
        return diff.hasDiff();
    }

}
