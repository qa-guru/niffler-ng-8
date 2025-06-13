package guru.qa.niffler.util;

import java.util.Locale;

public class FormatUtil {

    public static String format(double d) {
        Locale dotLocale = Locale.US;
        return (d % 1 == 0)
            ? String.format(dotLocale, "%.0f", d)
            : String.format(dotLocale, "%.2f", d);
    }
}
