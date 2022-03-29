package ru.javawebinar.topjava.util.formatters;

import org.springframework.format.Formatter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeFormatter implements Formatter<LocalTime> {
    @Override
    public LocalTime parse(String text, Locale locale) {
        return LocalTime.parse(text, DateTimeFormatter.ISO_TIME.withLocale(locale));
    }

    @Override
    public String print(LocalTime object, Locale locale) {
        return object == null ? null : object.format(DateTimeFormatter.ISO_TIME.withLocale(locale));
    }
}
