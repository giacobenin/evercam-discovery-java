package io.evercam;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class EvercamObject
{
    static final int CODE_OK = 200;
    static final int CODE_CREATE = 201;
    static final int CODE_UNAUTHORISED = 401;
    static final int CODE_FORBIDDEN = 403;
    static final int CODE_ERROR = 400;
    static final int CODE_NOT_FOUND = 404;
    static final int CODE_CONFLICT = 409;
    static final int CODE_SERVER_ERROR = 500;
    static final int CODE_APPLICATION_ERROR = 503;

    final String RTSP_PREFIX = "rtsp://";
    final String HTTP_PREFIX = "http://";

    JSONObject jsonObject;

    protected JSONObject getJsonObjectByString(String key) throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    protected JSONArray getJsonArrayByString(String key) throws EvercamException
    {
        try
        {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Retrieve string from jsonObject and return a valid string or an empty string.
     */
    protected String getStringNotNull(String key)
    {
        try
        {
            String jsonString = jsonObject.getString(key);
            if (!jsonString.equals("null"))
            {
                return jsonString;
            }
        } catch (JSONException e)
        {
            //Ignore exception, return empty string
        }
        return "";
    }

    public static Right getRightsFrom(CameraShareInterface shareInterface)
    {
        Right rights = null;

        if(shareInterface instanceof CameraShare)
        {
            rights = ((CameraShare) shareInterface).getRights();
        }
        else if(shareInterface instanceof CameraShareRequest)
        {
            rights = ((CameraShareRequest) shareInterface).getRights();
        }

        return rights;
    }

    @Override
    public String toString()
    {
        return String.format("<%s@%s id=%s> JSON: %s", this.getClass().getName(), System.identityHashCode(this), this.getIdString(), jsonObject.toString());
    }

    private Object getIdString()
    {
        try
        {
            return jsonObject.getString("id");
        } catch (SecurityException e)
        {
            return "";
        } catch (JSONException e)
        {
            return "";
        }
    }
}
