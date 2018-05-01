package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.InputStream;

/**
 * Preset control that move camera to home position
 */
public class PTZHome implements PTZControl {
    private final String cameraId;

    public PTZHome(String cameraId) {
        this.cameraId = cameraId;
    }

    /**
     * POST /cameras/{id}/ptz/home
     *
     * @return true if it successfully moved the camera
     * @throws PTZException if any error occurred
     */
    @Override
    public boolean move() throws PTZException {
        return moveToHome();
    }

    private boolean moveToHome() throws PTZException {
        String homeUrl = URL + '/' + cameraId + "/ptz/home";

        if (API.hasUserKeyPair()) {
            try {
                HttpResponse<InputStream> response = Unirest.post(homeUrl).queryString(API.userKeyPairMap()).asBinary();
                if (response.getStatus() == EvercamObject.CODE_CREATE) {
                    return true;
                } else {
                    throw new PTZException("Home move error with response code: " + response.getStatus());
                }
            } catch (EvercamException e) {
                throw new PTZException(e);
            } catch (UnirestException e) {
                throw new PTZException(e);
            }
        } else {
            throw new PTZException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
    }
}
