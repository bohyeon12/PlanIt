package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    // 오늘 날짜
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    // 해당 월의 1일이 무슨 요일인지에 따라, 달력 앞 공백 몇 칸인지 (0~6)
    // CalendarViewPanel에서 offset 계산할 때 사용
    public static int getFirstDayOffset(LocalDate month) {
        if (month == null) return 0;
        LocalDate first = month.withDayOfMonth(1);
        int dow = first.getDayOfWeek().getValue(); // 1=월 ... 7=일
        return dow % 7; // 일요일 => 0, 월요일 => 1 ...
    }

    // 그 달의 일 수
    public static int getDaysInMonth(LocalDate month) {
        if (month == null) return 0;
        return month.lengthOfMonth();
    }

    // 두 날짜가 같은지 (null-safe)
    public static boolean isSameDate(LocalDate a, LocalDate b) {
        if (a == null || b == null) return false;
        return a.isEqual(b);
    }

    // DB/폼에서 쓰는 기본 날짜 문자열 포맷 (yyyy-MM-dd)
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

    // UI에서 보기 좋게 출력 (예: "3월 5일")
    public static String dateToUiString(LocalDate date) {
        if (date == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M월 d일");
        return fmt.format(date);
    }
}
