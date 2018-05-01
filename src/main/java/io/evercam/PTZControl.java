package io.evercam;

public interface PTZControl {
    static String URL = API.URL + "cameras";

    boolean move() throws PTZException;
}
