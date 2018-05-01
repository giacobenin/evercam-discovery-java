package io.evercam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The base object for the sub containers(internal,external etc)
 * in Evercam camera model.
 */
class BaseCameraObject extends EvercamObject
{
    BaseCameraObject(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }


    /**
     * @return the 'host' URL in corresponding camera object.
     */
    public String getHost()
    {
        return getStringNotNull("host");
    }

    /**
     * @return the 'http' object in corresponding camera object.
     */
    public EvercamHttp getHttp() throws EvercamException
    {
        JSONObject httpJsonObject = getJsonObjectByString("http");
        return new EvercamHttp(httpJsonObject);
    }

    /**
     * @return the 'rtsp' object in corresponding camera object.
     */
    public EvercamRtsp getRtsp() throws EvercamException
    {
        JSONObject rtspJsonObject = getJsonObjectByString("rtsp");
        return new EvercamRtsp(rtspJsonObject);
    }
}

class EvercamHttp extends EvercamObject
{
    EvercamHttp(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public int getPort()
    {
        try
        {
            return jsonObject.getInt("port");
        } catch (JSONException e)
        {
            return 0;
        }
    }

    public String getCameraUrl()
    {
        return getStringNotNull("camera");
    }

    protected String getJpgUrl()
    {
        return getStringNotNull("jpg");
    }

    public String getMjpgUrl()
    {
        return getStringNotNull("mjpg");
    }
}

class EvercamRtsp extends EvercamObject
{
    EvercamRtsp(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    protected int getPort()
    {
        try
        {
            return jsonObject.getInt("port");
        } catch (JSONException e)
        {
            return 0;
        }
    }

    public String getMpegUrl()
    {
        return getStringNotNull("mpeg");
    }

    public String getAudioUrl()
    {
        return getStringNotNull("audio");
    }

    protected String getH264Url()
    {
        return getStringNotNull("h264");
    }
}

class Internal extends BaseCameraObject
{
    Internal(JSONObject jsonObject)
    {
        super(jsonObject);
    }
}

class External extends BaseCameraObject
{
    External(JSONObject jsonObject)
    {
        super(jsonObject);
    }
}

class ProxyUrl extends EvercamObject
{
    ProxyUrl(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public String getHls()
    {
        return jsonObject.getString("hls");
    }

    public String getRtmp()
    {
        return jsonObject.getString("rtmp");
    }
}

class Location extends EvercamObject
{
    Location(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    public float getLng()
    {
        return (float) jsonObject.getDouble("lng");
    }

    public float getLat()
    {
        return (float) jsonObject.getDouble("lat");
    }
}
