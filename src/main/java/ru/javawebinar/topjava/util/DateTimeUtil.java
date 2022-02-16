package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        LocalTime finalStartTime = startTime != null ? startTime : LocalTime.MIN;
        LocalTime finalEndTime = endTime != null ? endTime : LocalTime.MAX;
        return lt.compareTo(finalStartTime) >= 0 && lt.compareTo(finalEndTime) <= 0;
    }

    public static boolean isBetweenDates(LocalDate ld, LocalDate startDate, LocalDate endDate) {
        LocalDate finalStartDate = startDate != null ? startDate : LocalDate.of(1990, 1, 1);
        LocalDate finalEndDate = endDate != null ? endDate : LocalDate.of(2990, 1, 1);
        return ld.compareTo(finalStartDate) >= 0 && ld.compareTo(finalEndDate) <= 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}

