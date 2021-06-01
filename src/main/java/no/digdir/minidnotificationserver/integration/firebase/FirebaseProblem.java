package no.digdir.minidnotificationserver.integration.firebase;

import com.google.firebase.messaging.FirebaseMessagingException;
import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.util.Map;

public class FirebaseProblem extends AbstractThrowableProblem {

    public FirebaseProblem(FirebaseMessagingException e, Map<String, Object> parameterMap) {
        super(ErrorConstants.FIREBASE_TYPE,
                "Issue sending message through Firebase.", // title
                Status.BAD_REQUEST,  // status code
                e.getMessagingErrorCode() + ": " + e.getMessage(), // detail
                null, // instance
                null, // or e.getCause(),
                parameterMap
        );
    }

}

/* Example of response:
{
  "type": "https://digdir.no/problem/firebase",
  "title": "Issue sending message through Firebase.",
  "status": 400,
  "detail": "INVALID_ARGUMENT: The registration token is not a valid FCM registration token",
  "correlation_id": "16b27c42-5b02-4492-8447-1d2a44d05f74"
}
 */