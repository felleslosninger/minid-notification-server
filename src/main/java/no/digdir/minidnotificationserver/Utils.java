package no.digdir.minidnotificationserver;

import java.time.format.DateTimeFormatter;

public class Utils {
    public final static DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;  // .withZone(ZoneOffset.UTC)) for 'z' instead of '+01:00'
}
