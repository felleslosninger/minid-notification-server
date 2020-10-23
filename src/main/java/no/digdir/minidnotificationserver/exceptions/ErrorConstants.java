package no.digdir.minidnotificationserver.exceptions;

import java.net.URI;

public final class ErrorConstants {
    public static final String PROBLEM_BASE_URL = "https://digdir.no/problem";
    public static final URI REMOTE_API_TYPE = URI.create(PROBLEM_BASE_URL + "/remote-api");
    public static final URI INTERNAL_TYPE = URI.create(PROBLEM_BASE_URL + "/internal-error");
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");

}
