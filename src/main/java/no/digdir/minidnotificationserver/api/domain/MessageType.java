package no.digdir.minidnotificationserver.api.domain;

public enum MessageType {
    display("display"),
    data("data");

    String type;

    MessageType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
