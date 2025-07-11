package com.example.finance_control.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatDateTime {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withLocale(Locale.forLanguageTag("pt-BR"));
    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    public static String formatDate(Instant instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, BRAZIL_ZONE);
        return ldt.format(FORMATTER);
    }
}