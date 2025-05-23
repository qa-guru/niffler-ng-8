package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class CommonSteps {

    public static BufferedImage screenshot(SelenideElement element) throws IOException {
        return ImageIO.read(Objects.requireNonNull(Objects.requireNonNull(element).screenshot()));
    }
}
