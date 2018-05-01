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

public class CameraShare extends EvercamObject implements CameraShareInterface
{
    static String URL = API.URL + "cameras";

    CameraShare(JSONObject shareJSONObject)
    {
        this.jsonObject = shareJSONObject;
    }

    /**
     * POST /cameras/{id}/shares
     * Create a new camera share
     *
     * @param cameraId The unique identifier of the camera to share
     * @param user Email address or user name of the user to share the camera with.
     * @param rights A comma separate list of the rights to be granted with the share.
     * @param message Message to send in Email
     *
     * @return CameraShare object if the camera successfully shared with the user.
     * @throws EvercamException
     */
    public static CameraShareInterface create(String cameraId, String user, String rights, String message) throws
            EvercamException
    {
        CameraShare cameraShare = null;
        CameraShareRequest cameraShareRequest = null;
        Map<String, Object> fieldsMap = API.userKeyPairMap();
        fieldsMap.put("email", user);
        fieldsMap.put("rights", rights);
        if(message != null && !message.isEmpty())
        {
            fieldsMap.put("message", message);
        }

        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response =  Unirest.post(URL + '/' + cameraId + "/shares")
                        .header("accept", "application/json").fields(fieldsMap).asJson();
                if (response.getStatus() == CODE_CREATE)
                {
                    try
                    {
                        JSONObject jsonObject = response.getBody().getObject().getJSONArray("shares").getJSONObject(0);
                        cameraShare = new CameraShare(jsonObject);
                    }
                    catch (JSONException e)
                    {
                        JSONObject jsonObject = response.getBody().getObject().getJSONArray("share_requests")
                                .getJSONObject(0);
                        cameraShareRequest = new CameraShareRequest(jsonObject);
                        return cameraShareRequest;
                    }
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
            } catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
        return cameraShare;
    }

    /**
     * GET /cameras/{id}/shares
     * Get the list of shares for a specified camera
     *
     * @param cameraId The unique identifier for the camera in the share.
     * @param userId The unique identifier for the user the camera is shared with.
     * @return CameraShare object if the share exists, otherwise return null.
     * @throws EvercamException
     */
    public static CameraShare get(String cameraId, String userId) throws EvercamException
    {
        ArrayList<CameraShare> shareList = getSharesByUrl(URL + '/' + cameraId +  "/shares?user_id=" + userId);
        if(shareList.size() > 0)
        {
            return shareList.get(0);
        }
        return null;
    }

    /**
     * PATCH /cameras/{id}/shares
     * Update an existing camera share
     *
     * @param cameraId The unique identifier of the camera
     * @param user Email address or user name of the shared user
     * @param rights A comma separate list of the rights to be patched
     * @return CameraShare object after patch
     * @throws EvercamException
     */
    public static CameraShare patch(String cameraId, String user, String rights) throws EvercamException
    {
        CameraShare cameraShare = null;
        if(API.hasUserKeyPair())
        {
            try
            {
                Map<String, Object> fieldsMap = API.userKeyPairMap();
                fieldsMap.put("email", user);
                fieldsMap.put("rights", rights);
                HttpResponse<JsonNode> response = Unirest.patch(URL + '/' + cameraId + "/shares").queryString
                    (fieldsMap).fields(API.userKeyPairMap()).asJson();
                if(response.getStatus() == CODE_OK)
                {
                    JSONObject jsonObject = response.getBody().getObject().getJSONArray("shares").getJSONObject(0);
                    cameraShare = new CameraShare(jsonObject);
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

        return cameraShare;
    }

    /**
     * DELETE /cameras/{id}/shares
     * Delete an existing camera share
     *
     * @param userId  The unique identifier for the user the camera is shared with.
     * @param cameraId  The unique identifier for a camera.
     * @return true if camera share get successfully deleted
     * @throws EvercamException with error message if failed to delete camera share
     */
    public static boolean delete(String cameraId, String userId) throws EvercamException
    {
        if (API.hasUserKeyPair())
        {
            if(userId == null)
            {
                throw new NullPointerException("User id can not be null");
            }
            if(cameraId == null)
            {
                throw new NullPointerException("Camera id can not be null");
            }
            try
            {
                Map<String,Object> fieldsMap = API.userKeyPairMap();
                fieldsMap.put("email", userId);
                HttpResponse<JsonNode> response = Unirest.delete(URL + '/' + cameraId + "/shares")
                        .fields(fieldsMap).asJson();
                int statusCode = response.getStatus();
                if(statusCode == CODE_OK)
                {
                    return true;
                }
                else
                {
                    ErrorResponse errorResponse = new ErrorResponse(response.getBody().getObject());
                    throw new EvercamException(errorResponse.getMessage());
                }
            } catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
        } else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
    }

    /**
     * Return the list of shares for a specified camera
     *
     * @param cameraId The unique identifier for a camera.
     */
    public static ArrayList<CameraShare> getByCamera(String cameraId) throws EvercamException
    {
        return getSharesByUrl(URL + '/' + cameraId + "/shares");
    }

    private static ArrayList<CameraShare> getSharesByUrl(String url) throws EvercamException
    {
        ArrayList<CameraShare> cameraShares = new ArrayList<CameraShare>();
        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get(url).queryString(API.userKeyPairMap()).header("accept",
                        "application/json").asJson();
                if (response.getStatus() == CODE_OK)
                {
                    JSONArray sharesJSONArray = response.getBody().getObject().getJSONArray("shares");
                    for (int count = 0; count < sharesJSONArray.length(); count++)
                    {
                        JSONObject shareJSONObject = sharesJSONArray.getJSONObject(count);
                        cameraShares.add(new CameraShare(shareJSONObject));
                    }
                }
                else if (response.getStatus() == CODE_SERVER_ERROR)
                {
                    throw new EvercamException(EvercamException.MSG_SERVER_ERROR);
                }
                else
                {
                    ErrorResponse errorResponse = new ErrorResponse(response.getBody().getObject());
                    throw new EvercamException(errorResponse.getMessage());
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
        return cameraShares;
    }

    /**
     * @return Unique identifier for a camera share.
     * @throws EvercamException
     */
    public int getId() throws EvercamException
    {
        try
        {
            return jsonObject.getInt("id");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Unique identifier of the shared camera.
     */
    public String getCameraId()
    {
        return getStringNotNull("camera_id");
    }

    /**
     * @return The unique identifier of the user who shared the camera.
     */
    public String getSharerId()
    {
        return jsonObject.getString("sharer_id");
    }

    public String getSharerEmail()
    {
        return jsonObject.getString("sharer_email");
    }

    public String getSharerFullName()
    {
        return jsonObject.getString("sharer_name");
    }

    /**
     * Unique user id of the user the camera is shared with
     */
    public String getUserId()
    {
        return jsonObject.getString("user_id");
    }

    /**
     * Email of the user the camera is shared with
     */
    public String getUserEmail()
    {
        return getStringNotNull("email");
    }

    /**
     * Full name of the user the camera is shared with
     */
    public String getFullName()
    {
        return getStringNotNull("fullname");
    }

    /**
     * Either 'public' or 'private' depending on the share kind.,
     */
    public String getKind()
    {
        return jsonObject.getString("kind");
    }

    public Right getRights()
    {
        String rightsString = jsonObject.getString("rights");
        return new Right(rightsString);
    }

    @Override
    public String toString()
    {
        return jsonObject.toString();
    }
}
