package guru.qa.niffler.page;

import guru.qa.niffler.page.component.CategoryPicker;
import guru.qa.niffler.page.component.basicComponents.Button;
import guru.qa.niffler.page.component.basicComponents.LineEdit;
import org.openqa.selenium.By;

public class SpendingPage extends BasePage{

    public LineEdit amount = new LineEdit(By.cssSelector("#amount"));
    public LineEdit description = new LineEdit(By.cssSelector("#description"));
    public CategoryPicker category = new CategoryPicker(By.xpath("//ul"));
    public Button add = new Button(By.cssSelector("#save"));
}