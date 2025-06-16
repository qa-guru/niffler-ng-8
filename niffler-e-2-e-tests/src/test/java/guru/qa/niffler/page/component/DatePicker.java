package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.data.enums.Month;
import org.openqa.selenium.By;

import java.util.Calendar;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DatePicker {

    private By loc;

    private By yearsBtn = By.xpath("//div[contains(@class, 'MuiPickersFadeTransitionGroup')]");
    private By yearsCells = By.xpath("//div[contains(@class, 'viewTransitionContainer')]");
    private By daysCells = By.xpath("//div[contains(@class, 'MuiPickersSlideTransition')]");

    private By nextMouthBtn = By.xpath("//*[@data-testid='ArrowRightIcon']");
    private By previousMouthBtn = By.xpath("//*[@data-testid='ArrowLeftIcon']");


    public DatePicker(By locator) {
        loc = locator;
    }

    public DatePicker clickForOpenCalendar() {
        $(loc).click();
        return this;
    }

    public DatePicker setDate(Date date) {
        clickForOpenCalendar();
        setYear(String.valueOf(date.getYear()));
        setMonth(String.valueOf(date.getMonth()));
        setDay(date.getDay());
        return this;
    }

    public DatePicker setMonth(String month) {
        String monthText = $(yearsBtn).getText();
        Month targetMonth = getMonthFromString(month);
        String[] parts = monthText.split(" ");
        String displayedMonthText = parts[0];
        Month displayedMonth = getMonthFromString(displayedMonthText);

        if (displayedMonth == targetMonth) {
            return this;
        } else if (displayedMonth.getNumMonth() < targetMonth.getNumMonth()) {
            int difference = targetMonth.getNumMonth() - displayedMonth.getNumMonth();
            for (int j = 0; j <= difference; j++) {
                $(nextMouthBtn).click();
            }
            return this;
        } else if (displayedMonth.getNumMonth() > targetMonth.getNumMonth()) {
            int difference = displayedMonth.getNumMonth() - targetMonth.getNumMonth();
            for (int j = 0; j <= difference; j++) {
                $(previousMouthBtn).click();
            }
            return this;
        }
        return this;
    }

    public DatePicker setYear(String month) {
        $(yearsBtn).click();
        ElementsCollection cells = $$(yearsCells);
        cells = cells.filterBy(Condition.text(month));
        cells.shouldHave(CollectionCondition.sizeGreaterThan(0));
        cells.first().scrollTo();
        cells.first().click();
        return this;
    }

    public DatePicker setDay(Integer day) {
        ElementsCollection cells = $$(daysCells);
        cells = cells.filterBy(Condition.text(day.toString()));
        cells.shouldHave(CollectionCondition.sizeGreaterThan(0));
        if ((cells.size() == 1) || (day < 15)) {
            cells.first().click();
        } else
            cells.get(1).click();
        return this;
    }

    private Calendar getCurrentDate() {
        return Calendar.getInstance();
    }

    private Month getMonthFromString(String month) {
        return Month.valueOf(month);
    }
}
