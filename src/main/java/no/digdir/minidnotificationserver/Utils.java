package no.digdir.minidnotificationserver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Utils {
    public final static DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;  // .withZone(ZoneOffset.UTC)) for 'z' instead of '+01:00'

    public static String generateCodeChallange(String codeVerifier)  {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
