package guru.qa.niffler.web.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class Calendar<P> extends BaseComponent<P> {

    public Calendar(P currentPage) {
        super(currentPage, $("div.MuiInputBase-root input[name=\"date\"]"));
    }

    public Calendar<P> selectDateInCalendar(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = formatter.format(date);
        self.setValue(formattedDate);
        return this;
    }
}
