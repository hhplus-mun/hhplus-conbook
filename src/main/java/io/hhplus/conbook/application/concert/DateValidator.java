package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.interfaces.api.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator {
    public static void validateDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(ErrorCode.CONCERT_DATE_FOMRAT.getCode());
        }
    }
}
