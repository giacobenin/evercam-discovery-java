package io.evercam;

import com.mashape.unirest.http.exceptions.UnirestException;

public class PTZException extends Exception {
    public PTZException(EvercamException e) {
        super(e);
    }

    public PTZException(UnirestException e) {
        super(e);
    }

    public PTZException(String message) {
        super(message);
    }
}
