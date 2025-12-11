package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static int getFirstDayOffset(LocalDate month) {
        if (month == null) return 0;
        LocalDate first = month.withDayOfMonth(1);
        int dow = first.getDayOfWeek().getValue(); 
        return dow % 7; 
    }

    public static int getDaysInMonth(LocalDate month) {
        if (month == null) return 0;
        return month.lengthOfMonth();
    }

    public static boolean isSameDate(LocalDate a, LocalDate b) {
        if (a == null || b == null) return false;
        return a.isEqual(b);
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String dateToString(LocalDate date) {
        if (date == null) return "";
        return DATE_FMT.format(date);
    }

    public static LocalDate stringToDate(String text) {
        if (text == null) return null;
        String t = text.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalDate.parse(t, DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String dateToUiString(LocalDate date) {
        if (date == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M월 d일");
        return fmt.format(date);
    }
}
