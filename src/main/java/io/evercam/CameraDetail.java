package io.evercam;


public class CameraDetail {
    public String id;
    String name;
    Boolean isPublic;
    Boolean isOnline;
    String vendor;
    String model;
    String timezone;
    String macAddress;
    String jpgUrl;
    String mjpgUrl;
    String mpegUrl;
    String audioUrl;
    String h264Url;
    String internalHost;
    int internalHttpPort;
    int internalRtspPort;
    String externalHost;
    int externalHttpPort;
    int externalRtspPort;
    String cameraUsername;
    String cameraPassword;
    Float locationLng;
    Float locationLat;
    String locationLngString;
    String locationLatString;
    Boolean isDiscoverable;

    public CameraDetail(PatchCameraBuilder builder) {
        id = builder.id;
        name = builder.name;
        isPublic = builder.isPublic;
        isOnline = builder.isOnline;
        vendor = builder.vendor;
        model = builder.model;
        timezone = builder.timezone;
        macAddress = builder.macAddress;
        jpgUrl = builder.jpgUrl;
        h264Url = builder.h264Url;
        mjpgUrl = builder.mjpgUrl;
        mpegUrl = builder.mpegUrl;
        audioUrl = builder.audioUrl;
        internalHost = builder.internalHost;
        internalHttpPort = builder.internalHttpPort;
        internalRtspPort = builder.internalRtspPort;
        externalHost = builder.externalHost;
        externalHttpPort = builder.externalHttpPort;
        externalRtspPort = builder.externalRtspPort;
        cameraUsername = builder.cameraUsername;
        cameraPassword = builder.cameraPassword;
        //        locationLat = builder.locationLat;
        //        locationLng = builder.locationLng;
        locationLatString = builder.locationLat;
        locationLngString = builder.locationLng;
        isDiscoverable = builder.isDiscoverable;
    }

    public CameraDetail(CameraBuilder builder) {
        id = builder.id;
        name = builder.name;
        isPublic = builder.isPublic;
        isOnline = builder.isOnline;
        vendor = builder.vendor;
        model = builder.model;
        timezone = builder.timezone;
        macAddress = builder.macAddress;
        jpgUrl = builder.jpgUrl;
        h264Url = builder.h264Url;
        mjpgUrl = builder.mjpgUrl;
        mpegUrl = builder.mpegUrl;
        audioUrl = builder.audioUrl;
        internalHost = builder.internalHost;
        internalHttpPort = builder.internalHttpPort;
        internalRtspPort = builder.internalRtspPort;
        externalHost = builder.externalHost;
        externalHttpPort = builder.externalHttpPort;
        externalRtspPort = builder.externalRtspPort;
        cameraUsername = builder.cameraUsername;
        cameraPassword = builder.cameraPassword;
        locationLat = builder.locationLat;
        locationLng = builder.locationLng;
        isDiscoverable = builder.isDiscoverable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public String getVendor() {
        return vendor;
    }

    public String getModel() {
        return model;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getJpgUrl() {
        return jpgUrl;
    }

    public String getH264Url() {
        return h264Url;
    }

    public String getInternalHost() {
        return internalHost;
    }

    public int getInternalHttpPort() {
        return internalHttpPort;
    }

    public int getExternalHttpPort() {
        return externalHttpPort;
    }

    public String getExternalHost() {
        return externalHost;
    }

    public int getInternalRtspPort() {
        return internalRtspPort;
    }

    public int getExternalRtspPort() {
        return externalRtspPort;
    }

    public String getCameraUsername() {
        return cameraUsername;
    }

    public String getCameraPassword() {
        return cameraPassword;
    }

    public float getLocationLng() {
        return locationLng;
    }

    public float getLocationLat() {
        return locationLat;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public Boolean getIsDiscoverable() {
        return isDiscoverable;
    }

    @Override
    public String toString() {
        return "CameraDetail{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isPublic=" + isPublic +
                ", isOnline=" + isOnline +
                ", vendor='" + vendor + '\'' +
                ", model='" + model + '\'' +
                ", timezone='" + timezone + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", jpgUrl='" + jpgUrl + '\'' +
                ", mjpgUrl='" + mjpgUrl + '\'' +
                ", mpegUrl='" + mpegUrl + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", h264Url='" + h264Url + '\'' +
                ", internalHost='" + internalHost + '\'' +
                ", internalHttpPort=" + internalHttpPort +
                ", internalRtspPort=" + internalRtspPort +
                ", externalHost='" + externalHost + '\'' +
                ", externalHttpPort=" + externalHttpPort +
                ", externalRtspPort=" + externalRtspPort +
                ", cameraUsername='" + cameraUsername + '\'' +
                ", cameraPassword='" + cameraPassword + '\'' +
                ", locationLng=" + locationLng +
                ", locationLat=" + locationLat +
                ", isDiscoverable=" + isDiscoverable +
                '}';
    }
}
