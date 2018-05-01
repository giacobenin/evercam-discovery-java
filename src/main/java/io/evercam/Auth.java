package io.evercam;

import org.json.JSONException;
import org.json.JSONObject;

public class Auth
{
    private String type;
    private JSONObject authJSONObject = null;
    public static final String TYPE_BASIC = "basic";

    protected Auth(String type, JSONObject authJSONObject)
    {
        this.type = type;
        this.authJSONObject = authJSONObject;
    }


    public String getUsername() throws EvercamException
    {
        try
        {
            return authJSONObject.getString("username");
        } catch (JSONException e)
        {
            return "";
        }
    }

    public String getType()
    {
        return type;
    }

    public String getPassword() throws EvercamException
    {
        try
        {
            return authJSONObject.getString("password");
        } catch (JSONException e)
        {
            return "";
        }
    }
}
