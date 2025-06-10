package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Month;
import java.util.Date;
import java.util.Locale;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Calendar {
  private final SelenideElement self;

  private final String previousMonthButton = "button[title='Previous month']";
  private final String nextMonthButton = "button[title='Next month']";
  private final String yearsContainer = "div.MuiYearCalendar-root";
  private final String daysContainer = "div.MuiDayCalendar-monthContainer";
  private final String monthAndYearLabel = "div.MuiPickersCalendarHeader-label";
  private final String selectYearButton = "button[aria-label='calendar view is open, switch to year view']";


  public Calendar(SelenideElement self) {
    this.self = self;
  }

  public Calendar() {
    this.self = $("div.MuiDateCalendar-root");
  }

  @Step("Set date with calendar")
  public Calendar setDate(Date date) {
    setYear(date.getYear());
    setMonth(date.getMonth());
    setDay(date.getDate());
    return this;
  }

  @Step("Set year with calendar")
  public Calendar setYear(int year) {
    self.$(selectYearButton).click();
    self.$(yearsContainer)
        .$$("button")
        .findBy(text(String.valueOf(year + 1900)))
        .click();
    return this;
  }

  @Step("Set month with calendar")
  public Calendar setMonth(int targetMonth) {
    String currentMonthName = self.$(monthAndYearLabel).getText().split(" ")[0];
    Month currentMonthEnum = Month.valueOf(currentMonthName.toUpperCase(Locale.ENGLISH));
    int monthIndex = currentMonthEnum.getValue() - 1;
    while (monthIndex != targetMonth) {
      if (monthIndex < targetMonth) {
        self.$(nextMonthButton).click();
        monthIndex++;
      } else {
        self.$(previousMonthButton).click();
        monthIndex--;
      }
    }
    return this;
  }

  @Step("Set day with calendar")
  public Calendar setDay(int day) {
    self.$(daysContainer)
        .$$("button")
        .findBy(exactOwnText(String.valueOf(day)))
        .click();
    return this;
  }
}