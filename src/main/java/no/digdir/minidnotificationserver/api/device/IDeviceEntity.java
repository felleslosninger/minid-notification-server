package no.digdir.minidnotificationserver.api.device;


public interface IDeviceEntity {
    String getToken();
    String getApns_token();
    String getApp_identifier();
    String getApp_version();
    String getOs();
    String getOs_version();
}
