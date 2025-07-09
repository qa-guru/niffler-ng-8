package guru.qa.niffler.utils;

import guru.qa.niffler.model.SpendJson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputGenerator {

    public static String getExpectedBubbleText(SpendJson spendJson) {
        return getExpectedBubbleText(spendJson.category().name(), spendJson.amount());
    }

    public static String getExpectedBubbleText(String categoryName, Double amount) {
        int lastDotIndex = amount.toString().lastIndexOf('.');
        if (lastDotIndex != -1) {
            return categoryName + " " + amount.toString().substring(0, lastDotIndex) + " ₽";
        }
        return categoryName + " " + amount.toString() + " ₽";
    }

    public static String getExpectedSpendAmount(Double amount) {
        int lastDotIndex = amount.toString().lastIndexOf('.');
        if (lastDotIndex != -1) {
            return amount.toString().substring(0, lastDotIndex) + " ₽";
        }
        return amount.toString() + " ₽";
    }

    public static String getExpectedSpendDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }
}
