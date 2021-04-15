package no.digdir.minidnotificationserver.logging.event;

import no.idporten.domain.log.LogEntry;
import no.idporten.domain.log.LogEntryData;
import no.idporten.domain.log.LogEntryLogType;
import no.idporten.log.event.EventLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class EventService {

    private static final String MNS_ISSUER_ID = "MINID-NOTIFICATION-SERVER";
    static final LogEntryLogType MNS_DEVICE_REGISTERED = new LogEntryLogType("MNS_REGISTER_DEVICE","User has registerd a new device");
    static final LogEntryLogType MNS_DEVICE_UPDATED = new LogEntryLogType("MNS_UPDATE_DEVICE","User`s device has been updated");
    static final LogEntryLogType MNS_DEVICE_DELETED = new LogEntryLogType("MNS_DELETE_DEVICE","User`s device has been deleted");

    private final EventLogger eventLogger;

    @Autowired
    public EventService(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    public void logUserHasRegisteredDevice(String personIdentifier){
        eventLogger.log(logEntry(MNS_DEVICE_REGISTERED,personIdentifier));
    }

    public void logUserHasUpdatedDevice(String personIdentifier){
        eventLogger.log(logEntry(MNS_DEVICE_UPDATED,personIdentifier));
    }

    public void logUserHasDeletedDevice(String personIdentifier){
        eventLogger.log(logEntry(MNS_DEVICE_DELETED,personIdentifier));
    }

    private LogEntry logEntry(LogEntryLogType logType, String personIdentifier, LogEntryData... logEntryData) {
        LogEntry logEntry = new LogEntry(logType);
        logEntry.setIssuer(MNS_ISSUER_ID);
        logEntry.setPersonIdentifier(personIdentifier);
        logEntry.addAllLogEntryData(Arrays.asList(logEntryData));
        return logEntry;
    }

}
