package no.digdir.minidnotificationserver.exceptions;

import java.net.URI;

public final class ErrorConstants {
    private static final String PROBLEM_BASE_URL = "https://digdir.no/problem";

    public static final URI REMOTE_API_TYPE = URI.create(PROBLEM_BASE_URL + "/remote-api");
    public static final URI INTERNAL_TYPE = URI.create(PROBLEM_BASE_URL + "/internal-error");
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI UNKNOWN_TYPE = URI.create(PROBLEM_BASE_URL + "/unknown-error");
    public static final URI FIREBASE_TYPE = URI.create(PROBLEM_BASE_URL + "/firebase");
    public static final URI GOOGLE_TYPE = URI.create(PROBLEM_BASE_URL + "/google-api");
    public static final URI MINID_AUTH_TYPE = URI.create(PROBLEM_BASE_URL + "/minid-authentication-service");
    public static final URI MINID_BACKEND_TYPE = URI.create(PROBLEM_BASE_URL + "/minid-backend-service");
    public static final URI NETS_TYPE = URI.create(PROBLEM_BASE_URL + "/nets-ciba-service");
    public static final URI NOTFOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/not-found");
    public static final URI ONBOARDING_TYPE = URI.create(PROBLEM_BASE_URL + "/onboarding");
    public static final URI OTC_FAILED = URI.create(PROBLEM_BASE_URL + "/otc-failed");
    public static final URI APP_VERSION_TYPE = URI.create(PROBLEM_BASE_URL + "/version");
    public static final URI APP_REQUIRED_VERSION_TYPE = URI.create(PROBLEM_BASE_URL + "/required-version");
    public static final URI ATTESTATION_REQUIRED_TYPE = URI.create(PROBLEM_BASE_URL + "/attestation-required");

}
