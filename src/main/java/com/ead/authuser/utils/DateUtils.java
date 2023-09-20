package com.ead.authuser.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {
    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
