package io.github.nikoir.series.tracker.content.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateUtils {

    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static Date stringToDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }

        try {
            LocalDate localDate = LocalDate.parse(dateString, ISO_DATE_FORMATTER);
            return Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return null;
        }
    }
}