package guru.qa.niffler.allure;

import guru.qa.niffler.util.IOUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.Base64;

@ParametersAreNonnullByDefault
public record ScreenDiff(String expected, String actual, String diff) {

    private static final String PNG_BASE64 = "data:image/png;base64,";

    public static @Nonnull ScreenDiff of(BufferedImage expected, BufferedImage actual, BufferedImage diff) {
        Base64.Encoder encoder = Base64.getEncoder();
        String exp = toEncodString(encoder, expected);
        String act = toEncodString(encoder, actual);
        String dif = toEncodString(encoder, diff);
        return new ScreenDiff(exp, act, dif);
    }

    private static @Nonnull String toEncodString(Base64.Encoder encoder, BufferedImage image) {
        return PNG_BASE64 + encoder.encodeToString(IOUtil.writeImageToByteArr(image));
    }
}
