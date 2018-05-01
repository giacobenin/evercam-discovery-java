package io.evercam;

import org.json.JSONException;
import org.json.JSONObject;

public class Defaults extends EvercamObject
{
    Defaults(JSONObject defaultsJSONObject)
    {
        this.jsonObject = defaultsJSONObject;
    }

    public String getJpgURL() throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject("snapshots").getString("jpg");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public String getH264URL() throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject("snapshots").getString("h264");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public String getLowresURL() throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject("snapshots").getString("lowres");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public String getMpeg4URL() throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject("snapshots").getString("mpeg4");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public String getMobileURL() throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject("snapshots").getString("mobile");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public String getMjpgURL() throws EvercamException
    {
        try
        {
            return jsonObject.getJSONObject("snapshots").getString("mjpg");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public Auth getAuth(String type) throws EvercamException
    {
        Auth auth;
        try
        {
            JSONObject authJSONObject = jsonObject.getJSONObject("auth").getJSONObject(type);
            auth = new Auth(type, authJSONObject);
        } catch (JSONException e)
        {
            return null;
        }
        return auth;
    }
}
