package guru.qa.niffler.page;

import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.page.component.basicComponents.LineEdit;
import org.openqa.selenium.By;

public class MainPage extends BasePage {

    public StatComponent stat = new StatComponent(By.xpath("//canvas[@role='img']"));
    public SpendingTable table = new SpendingTable();
    public LineEdit search = new LineEdit(By.xpath("//input"));
    public final StatComponent bubbles = new StatComponent(By.cssSelector("#legend-container"));

}
