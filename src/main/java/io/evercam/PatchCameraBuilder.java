package io.evercam;

public class PatchCameraBuilder
{
    final String id;
    Boolean isPublic;
    Boolean isOnline;
    String name;
    String vendor;
    String model;
    String timezone;
    String macAddress;
    String jpgUrl;
    String h264Url;
    String mjpgUrl;
    String mpegUrl;
    String audioUrl;
    String internalHost;
    int internalHttpPort;
    int internalRtspPort;
    String externalHost;
    int externalHttpPort;
    int externalRtspPort;
    String cameraUsername;
    String cameraPassword;
    String locationLng;
    String locationLat;
    Boolean isDiscoverable;

    public PatchCameraBuilder(String cameraId)
    {
        if (cameraId != null)
        {
            id = cameraId;
        }
        else
        {
            throw new NullPointerException("camera id can not be null");
        }
    }

    public PatchCameraBuilder setName(String cameraName)
    {
        name = cameraName;
        return this;
    }

    public PatchCameraBuilder setPublic(Boolean isPublic)
    {
        this.isPublic = isPublic;
        return this;
    }

    public PatchCameraBuilder setOnline(Boolean isOnline)
    {
        this.isOnline = isOnline;
        return this;
    }

    /**
     * @param lat GPS latitude coordinate of the camera,
     * @param lng GPS longitude coordinate of the camera
     */
    public PatchCameraBuilder setLocation(String lat, String lng)
    {
        this.locationLat = lat;
        this.locationLng = lng;
        return this;
    }

    public PatchCameraBuilder setJpgUrl(String jpgUrl)
    {
        this.jpgUrl = jpgUrl;
        return this;
    }

    public PatchCameraBuilder setH264Url(String h264Url)
    {
        this.h264Url = h264Url;
        return this;
    }

    public PatchCameraBuilder setMjpgUrl(String mjpgUrl)
    {
        this.mjpgUrl = mjpgUrl;
        return this;
    }

    public PatchCameraBuilder setMpegUrl(String mpegUrl)
    {
        this.mpegUrl = mpegUrl;
        return this;
    }

    public PatchCameraBuilder setAudioUrl(String audioUrl)
    {
        this.audioUrl = audioUrl;
        return this;
    }

    public PatchCameraBuilder setInternalHost(String internalHost)
    {
        this.internalHost = internalHost;
        return this;
    }

    public PatchCameraBuilder setInternalHttpPort(int internalHttpPort)
    {
        this.internalHttpPort = internalHttpPort;
        return this;
    }

    public PatchCameraBuilder setInternalRtspPort(int internalRtspPort)
    {
        this.internalRtspPort = internalRtspPort;
        return this;
    }

    public PatchCameraBuilder setExternalHost(String externalHost)
    {
        this.externalHost = externalHost;
        return this;
    }

    public PatchCameraBuilder setExternalHttpPort(int externalHttpPort)
    {
        this.externalHttpPort = externalHttpPort;
        return this;
    }

    public PatchCameraBuilder setExternalRtspPort(int externalRtspPort)
    {
        this.externalRtspPort = externalRtspPort;
        return this;
    }

    public PatchCameraBuilder setCameraUsername(String cameraUsername)
    {
        this.cameraUsername = cameraUsername;
        return this;
    }

    public PatchCameraBuilder setCameraPassword(String cameraPassword)
    {
        this.cameraPassword = cameraPassword;
        return this;
    }

    public PatchCameraBuilder setVendor(String vendor)
    {
        this.vendor = vendor;
        return this;
    }

    public PatchCameraBuilder setModel(String model)
    {
        this.model = model;
        return this;
    }

    public PatchCameraBuilder setTimeZone(String timezone)
    {
        this.timezone = timezone;
        return this;
    }

    public PatchCameraBuilder setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
        return this;
    }

    public PatchCameraBuilder setDiscoverable(Boolean isDiscoverable)
    {
        this.isDiscoverable = isDiscoverable;
        return this;
    }

    public CameraDetail build()
    {
        return new CameraDetail(this);
    }
}
