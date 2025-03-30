package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage extends BasePage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement newSpendingBtn = $x("//a[contains(text(), 'New spending')]");
    private final SelenideElement statistics =  $x("//div[@id='stat']");
    private final SelenideElement spendings =  $x("//div[@id='spendings']");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    public void assertMainPageOpened() {
        newSpendingBtn.shouldBe(visible.because("Должна отображаться кнопка New Spending"), Duration.ofSeconds(5));
        statistics.shouldBe(visible.because("Блок Statistics должен быть доступен"), Duration.ofSeconds(5));
        spendings.shouldBe(visible.because("Блок History of Spendings должен быть доступен"), Duration.ofSeconds(5));
    }


}
