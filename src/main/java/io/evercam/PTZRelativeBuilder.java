package io.evercam;

public class PTZRelativeBuilder {
    private final String cameraId;
    private int relativeUp = 0;
    private int relativeDown = 0;
    private int relativeLeft = 0;
    private int relativeRight = 0;
    private int relativeZoom = 0;

    public PTZRelativeBuilder(String cameraId) {
        this.cameraId = cameraId;
    }

    public PTZRelativeBuilder up(int value) {
        relativeUp = value;
        return this;
    }

    public PTZRelativeBuilder down(int value) {
        relativeDown = value;
        return this;
    }

    public PTZRelativeBuilder left(int value) {
        relativeLeft = value;
        return this;
    }

    public PTZRelativeBuilder right(int value) {
        relativeRight = value;
        return this;
    }

    public PTZRelativeBuilder zoom(int value) {
        relativeZoom = value;
        return this;
    }

    public PTZRelative build() {
        return new PTZRelative(this);
    }

    public String getCameraId() {
        return cameraId;
    }

    public int getRelativeUp() {
        return relativeUp;
    }

    public int getRelativeDown() {
        return relativeDown;
    }

    public int getRelativeLeft() {
        return relativeLeft;
    }

    public int getRelativeRight() {
        return relativeRight;
    }

    public int getRelativeZoom() {
        return relativeZoom;
    }
}
