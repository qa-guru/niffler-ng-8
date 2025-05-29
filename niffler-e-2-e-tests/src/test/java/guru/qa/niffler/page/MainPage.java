package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Popup;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.basicComponents.LineEdit;
import guru.qa.niffler.utils.CommonSteps;
import org.openqa.selenium.By;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class MainPage {

    private final SelenideElement stat = $("canvas[role='img']");


    public SpendingTable table = new SpendingTable();
    public Popup popup = new Popup();
    public LineEdit search = new LineEdit(By.xpath("//input"));

    public BufferedImage screenshotStats() throws IOException {
        sleep(5000);
        return CommonSteps.screenshot(stat);
    }
}
