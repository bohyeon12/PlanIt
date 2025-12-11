package util;

import java.time.LocalDate;

public class Validator {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isValidDate(String text) {
        LocalDate d = DateUtils.stringToDate(text);
        return d != null;
    }
}
