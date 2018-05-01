package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User extends EvercamObject
{

    private static String URL = API.URL + "users";


    User(JSONObject userJSONObject)
    {
        this.jsonObject = userJSONObject;
    }

    /**
     * Return two letter ISO country code of the user.
     *
     * @throws EvercamException
     */
    public String getCountry() throws EvercamException
    {
        try
        {
            return jsonObject.getString("country");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return unique Evercam username of the user.
     *
     * @throws EvercamException
     */
    public String getId() throws EvercamException
    {
        try
        {
            return jsonObject.getString("id");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return Email address of the user.
     *
     * @throws EvercamException
     */
    public String getEmail() throws EvercamException
    {
        try
        {
            return jsonObject.getString("email");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return last name of the user.
     */
    public String getLastName()
    {
        return jsonObject.getString("lastname");
    }

    /**
     * Return first name of the user.
     */
    public String getFirstName()
    {
        return jsonObject.getString("firstname");
    }

    public String getFullName()
    {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Return unique Evercam username of the user.
     *
     * @throws EvercamException
     */
    public String getUsername() throws EvercamException
    {
        try
        {
            return jsonObject.getString("username");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Returns available information for a user by specifying user unique identifier.
     *
     * @param id unique Evercam username or Email address of the user.
     * @throws EvercamException if no user API key pair added
     */
    public User(String id) throws EvercamException
    {
        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get(URL + "/" + id).queryString(API.userKeyPairMap()).header
                        ("accept", "application/json").asJson();
                if (response.getStatus() == CODE_OK)
                {
                    JSONObject userJSONObject = response.getBody().getObject().getJSONArray("users").getJSONObject(0);
                    this.jsonObject = userJSONObject;
                }
                else if (response.getStatus() == CODE_FORBIDDEN || response.getStatus() == CODE_UNAUTHORISED)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
                }
                else if (response.getStatus() == CODE_NOT_FOUND)
                {
                    throw new EvercamException(response.getBody().getObject().getString("message"));
                }
                else
                {
                    throw new EvercamException(response.getBody().toString());
                }
            } catch (UnirestException e)
            {
                throw new EvercamException(e);
            } catch (JSONException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
    }

    /**
     * Starts the new user sign up process with Evercam
     *
     * @param userDetail user detail object with all details for the new user
     * @throws EvercamException
     */
    public static User create(UserDetail userDetail) throws EvercamException
    {
        User user = null;
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("firstname", userDetail.getFirstname());
        userMap.put("lastname", userDetail.getLastname());
        userMap.put("email", userDetail.getEmail());
        userMap.put("username", userDetail.getUsername());
        if(userDetail.hasCountryCode())
        {
            userMap.put("country", userDetail.getCountryCode());
        }

        userMap.put("password", userDetail.getPassword());

        try
        {
            HttpResponse<JsonNode> response = Unirest.post(URL).header("accept", "application/json").fields(userMap).asJson();
            if (response.getStatus() == CODE_CREATE)
            {
                JSONObject userJSONObject = response.getBody().getObject().getJSONArray("users").getJSONObject(0);
                user = new User(userJSONObject);
            }
            else if (response.getStatus() == CODE_UNAUTHORISED || response.getStatus() == CODE_FORBIDDEN)
            {
                throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
            }
            else
            {
                //The HTTP error code could be 400, 409 etc.
                ErrorResponse errorResponse = new ErrorResponse(response.getBody().getObject());
                throw new EvercamException(errorResponse.getMessage());
            }
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        } catch (UnirestException e)
        {
            throw new EvercamException(e);
        }

        return user;
    }

    /**
     * DELETE /users/{id}
     * Delete your account, any cameras you own and all stored media
     *
     * @param userId the unique identifier of the user to delete
     * @return true if the user account is successfully deleted
     * @throws EvercamException if user API key and id not specified
     */
    public static boolean delete(String userId) throws EvercamException
    {
        if(API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.delete(URL + '/' + userId).fields(API.userKeyPairMap())
                    .asJson();
                if(response.getStatus() == CODE_OK)
                {
                    return true;
                }
                else
                {
                    ErrorResponse errorResponse = new ErrorResponse(response.getBody().getObject());
                    throw new EvercamException(errorResponse.getMessage());
                }
            }
            catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
    }
}
