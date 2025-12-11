package util;

import java.time.LocalDate;

public class Validator {

    // 비어있는 문자열인지 (null-safe)
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // "yyyy-MM-dd" 형식이 맞는지 (DateUtils 사용)
    public static boolean isValidDate(String text) {
        LocalDate d = DateUtils.stringToDate(text);
        return d != null;
    }
}
