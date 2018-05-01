package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CameraShareRequest extends EvercamObject implements CameraShareInterface
{
    public final static String STATUS_PENDING = "PENDING";
    public final static String STATUS_USED = "USED";
    public final static String STATUS_CANCELLED = "CANCELLED";

    static String URL = API.URL + "cameras";

    CameraShareRequest(JSONObject shareJSONObject)
    {
        this.jsonObject = shareJSONObject;
    }

    /**
     * GET /cameras/{id}/shares/requests
     * Fetch the list of share requests currently outstanding for a given camera
     *
     * @param cameraId The unique identifier of the camera to fetch share requests for.
     * @param status The request status to fetch, either 'PENDING', 'USED' or 'CANCELLED'.
     * @return List of CameraShareRequest
     * @throws EvercamException
     */
    public static ArrayList<CameraShareRequest> get(String cameraId, String status) throws EvercamException
    {
        ArrayList<CameraShareRequest> shareRequestList = new ArrayList<CameraShareRequest>();

        if(API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get(getUrl(cameraId))
                        .queryString(API.userKeyPairMap()).queryString("status", status).asJson();

                if(response.getStatus() == CODE_OK)
                {
                    JSONArray sharesJSONArray = response.getBody().getObject().getJSONArray("share_requests");
                    for (int count = 0; count < sharesJSONArray.length(); count++)
                    {
                        JSONObject shareJSONObject = sharesJSONArray.getJSONObject(count);
                        shareRequestList.add(new CameraShareRequest(shareJSONObject));
                    }
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

        return shareRequestList;
    }

    /**
     * PATCH /cameras/{id}/shares/requests
     * Updates a pending camera share request
     *
     * @param cameraId The unique identifier of the camera share request to update
     * @param email The email address of user the camera was shared with.
     * @param rights The new set of rights to be granted for the share.
     * @return The share request after patching
     * @throws EvercamException
     */
    public static CameraShareRequest patch(String cameraId, String email, String rights) throws EvercamException
    {
        CameraShareRequest cameraShareRequest = null;
        if(API.hasUserKeyPair())
        {
            try
            {
                Map<String, Object> fieldsMap = API.userKeyPairMap();
                fieldsMap.put("email", email);
                fieldsMap.put("rights", rights);
                HttpResponse<JsonNode> response = Unirest.patch(getUrl(cameraId)).queryString
                        (fieldsMap).fields(API.userKeyPairMap()).asJson();
                if(response.getStatus() == CODE_OK)
                {
                    JSONObject jsonObject = response.getBody().getObject().getJSONArray("share_requests").getJSONObject
                            (0);
                    cameraShareRequest = new CameraShareRequest(jsonObject);
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

        return cameraShareRequest;
    }

    /**
     * DELETE /cameras/{id}/shares/requests
     * Cancels a pending camera share request for a given camera
     *
     * @param cameraId 	The unique identifier of the camera to fetch share requests for.
     * @param email The email address of user the camera was shared with.
     * @return true if the pending request deleted successfully
     * @throws EvercamException
     */
    public static boolean delete(String cameraId, String email) throws EvercamException
    {
        if (API.hasUserKeyPair())
        {
            if(email == null)
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
                fieldsMap.put("email", email);
                HttpResponse<JsonNode> response = Unirest.delete(getUrl(cameraId))
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
     * @return Unique identifier for a camera share request
     */
    public String getId()
    {
        return getStringNotNull("id");
    }

    /**
     * @return Unique identifier of the camera to be shared
     */
    public String getCameraId()
    {
        return getStringNotNull("camera_id");
    }

    /**
     * @return The unique identifier of the user who shared the camera
     */
    public String getUserId()
    {
        return getStringNotNull("user_id");
    }

    /**
     * @return Full name of the user who shared the camera
     */
    public String getSharerName()
    {
        return getStringNotNull("sharer_name");
    }

    /**
     * Email address of the user who shared the camera
     */
    public String getSharerEmail()
    {
        return getStringNotNull("sharer_email");
    }

    /**
     * @return The email address of the user the camera is shared with
     */
    public String getEmail()
    {
        return getStringNotNull("email");
    }

    /**
     * @return The rights to be granted on the share
     */
    public Right getRights()
    {
        String rightsString = jsonObject.getString("rights");
        return new Right(rightsString);
    }

    /**
     * @return API endpoint URL that contains camera id
     */
    private static String getUrl(String cameraId)
    {
        return URL + '/' + cameraId + "/shares/requests";
    }
}
