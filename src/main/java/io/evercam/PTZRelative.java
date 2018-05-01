package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.InputStream;

public class PTZRelative implements PTZControl
{
    private final String cameraId;
    private final int relativeLeft;
    private final int relativeRight;
    private final int relativeUp;
    private final int relativeDown;
    private final int relativeZoom;

    protected PTZRelative(PTZRelativeBuilder builder)
    {
        this.cameraId = builder.getCameraId();
        relativeLeft = builder.getRelativeLeft();
        relativeRight = builder.getRelativeRight();
        relativeUp = builder.getRelativeUp();
        relativeDown = builder.getRelativeDown();
        relativeZoom = builder.getRelativeZoom();
    }

    /**
     *
     * @return
     * @throws PTZException if any error occurred
     */
    @Override
    public boolean move() throws PTZException
    {
        return relativeMove(relativeLeft, relativeRight, relativeUp, relativeDown, relativeZoom);
    }

    /**
     * POST /cameras/{id}/ptz/relative
     *
     * @param left move the camera left
     * @param right move the camera right
     * @param up move the camera up
     * @param down move the camera down
     * @param zoom digital zoom the camera in(eg, 1) and out(eg, 2)
     * @return true if it successfully moved the camera
     * @throws PTZException
     */
    private boolean relativeMove(int left, int right, int up, int down, int zoom) throws PTZException
    {
        if(API.hasUserKeyPair())
        {
            String relativeMoveUrl = URL + '/' + cameraId + "/ptz/relative";

            System.out.println(relativeMoveUrl);

            try
            {
                HttpResponse<InputStream> response = Unirest.post(relativeMoveUrl).queryString(API.userKeyPairMap())
                        .field("left", left).field("right", right).field("up", up).field("down", down)
                        .field("zoom", zoom).asBinary();

                if (response.getStatus() == EvercamObject.CODE_CREATE)
                {
                    return true;
                }
                else
                {
                    throw new PTZException("Relative move error with response code: " + response.getStatus());
                }
            }
            catch (EvercamException e)
            {
                throw new PTZException(e);
            }
            catch (UnirestException e)
            {
                throw new PTZException(e);
            }
        }
        else
        {
            throw new PTZException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
    }
}
