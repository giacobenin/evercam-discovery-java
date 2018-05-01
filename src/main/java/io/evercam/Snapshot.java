package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class Snapshot extends EvercamObject
{
    static String URL = API.URL + "cameras";

    Snapshot(JSONObject snapshotJsonObject)
    {
        jsonObject = snapshotJsonObject;
    }

    /**
     * Fetches a snapshot from the camera and stores it using the current timestamp
     *
     * @param cameraId the camera's unique identifier with Evercam
     * @param notes    optional text note for this snapshot, if set to null, no text notes will
     *                 be saved with this camera.
     * @return the saved snapshot
     * @throws EvercamException if unable to save the snapshot
     */
    public static Snapshot record(String cameraId, String notes) throws EvercamException
    {
        Snapshot snapshot = null;
        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response;
                if (notes == null)
                {
                    response = Unirest.post(URL + '/' + cameraId + "/recordings/snapshots").fields(API.userKeyPairMap()).asJson();
                }
                else
                {
                    response = Unirest.post(URL + '/' + cameraId + "/recordings/snapshots").fields(API.userKeyPairMap()).field("notes", notes).asJson();
                }

                if (response.getStatus() == CODE_CREATE)
                {
                    JSONObject snapshotJsonObject = response.getBody().getObject().getJSONArray("snapshots").getJSONObject(0);
                    snapshot = new Snapshot(snapshotJsonObject);
                }
                else if (response.getStatus() == CODE_NOT_FOUND)
                {
                    throw new EvercamException("camera does not exist");
                }
                else if (response.getStatus() == CODE_ERROR)
                {
                    throw new EvercamException("camera is offline");
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
            }
            catch (JSONException e)
            {
                throw new EvercamException(e);
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
        return snapshot;
    }

    /**
     * A wrap of GET /cameras/{id}/recordings/snapshots, returns all pages in one method
     * <p/>
     * Returns the list of all snapshots currently stored for specific camera
     *
     * @param cameraId the unique identifier of the camera
     * @throws EvercamException if user key and id not specified
     */
    public static ArrayList<Snapshot> getRecordedSnapshots(String cameraId, int from, int to) throws EvercamException
    {
        ArrayList<Snapshot> snapshotList = new ArrayList<Snapshot>();

        SnapshotsWithPaging snapshotsWithPaging = getSnapshotListWithPaging(cameraId, from, to, 100, 1);
        snapshotList.addAll(snapshotsWithPaging.getSnapshotsList());
        int pages = snapshotsWithPaging.getTotalPages();

        if (pages > 1)
        {
            for (int index = 2; index < pages; index++)
            {
                SnapshotsWithPaging moreWithPaging = getSnapshotListWithPaging(cameraId, from, to, 100, index);
                snapshotList.addAll(moreWithPaging.getSnapshotsList());
            }
        }

        return snapshotList;
    }

    /**
     * GET /cameras/{id}/recordings/snapshots
     * <p/>
     * Returns the list of snapshots currently stored for specific camera with specific page
     *
     * @param cameraId the unique identifier of the camera
     * @throws EvercamException if user key and id not specified
     */
    public static SnapshotsWithPaging getSnapshotListWithPaging(String cameraId, int from, int to, int limit, int page) throws EvercamException
    {
        if (API.hasUserKeyPair())
        {
            Map<String, Object> fieldsMap = API.userKeyPairMap();
            fieldsMap.put("from", from);
            fieldsMap.put("to", to);
            fieldsMap.put("limit", limit);
            fieldsMap.put("page", page);

            try
            {
                HttpResponse<JsonNode> response = Unirest.get(URL + "/" + cameraId + "/recordings/snapshots").queryString
                        (fieldsMap).header("accept", "application/json").asJson();
                if (response.getStatus() == CODE_OK)
                {
                    return new SnapshotsWithPaging(response.getBody().getObject());
                }
                else if (response.getStatus() == CODE_UNAUTHORISED || response.getStatus() == CODE_FORBIDDEN)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
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
            }
            catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
            catch (JSONException e)
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
     * GET /cameras/{id}/recordings/snapshots/{year}/{month}/{day}/hours
     * <p/>
     * Returns list of specific hours in a given day which contains any snapshots
     *
     * @param cameraId the camera's unique identifier with Evercam
     * @param year     year, for example 2013
     * @param month    month, for example 11
     * @param day      day, for example 17
     * @throws EvercamException if user key pair is not specified
     */
    public static ArrayList<Integer> getHoursContainSnapshots(String cameraId, int year, int month, int day) throws EvercamException
    {
        ArrayList<Integer> hoursList = new ArrayList<Integer>();
        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get(URL + "/" + cameraId + "/recordings/snapshots/" + year 
                        + '/' + month + '/' + day + "/hours").queryString(API.userKeyPairMap()).header("accept", 
                        "application/json").asJson();

                if (response.getStatus() == CODE_OK)
                {
                    JSONObject hoursArrayObject = response.getBody().getObject();
                    JSONArray hoursArray = hoursArrayObject.getJSONArray("hours");

                    if (hoursArray.length() != 0)
                    {
                        for (int index = 0; index < hoursArray.length(); index++)
                        {
                            hoursList.add(hoursArray.getInt(index));
                        }
                    }
                }
                else if (response.getStatus() == CODE_UNAUTHORISED || response.getStatus() == CODE_FORBIDDEN)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
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
            }
            catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
            catch (JSONException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }

        return hoursList;
    }

    /**
     * GET /cameras/{id}/recordings/snapshots/{year}/{month}/days
     * <p/>
     * Returns list of specific days in a given month which contains any snapshots
     *
     * @param cameraId the camera's unique identifier with Evercam
     * @param year     year, for example 2013
     * @param month    month, for example 11
     * @throws EvercamException if user key pair is not specified
     */
    public static ArrayList<Integer> getDaysContainSnapshots(String cameraId, int year, int month) throws EvercamException
    {
        ArrayList<Integer> daysList = new ArrayList<Integer>();
        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get(URL + "/" + cameraId + "/recordings/snapshots/" + year
                        + '/' + month + "/days").queryString(API.userKeyPairMap()).header("accept", "application/json")
                        .asJson();

                if (response.getStatus() == CODE_OK)
                {
                    JSONObject daysArrayObject = response.getBody().getObject();
                    JSONArray daysJsonArray = daysArrayObject.getJSONArray("days");

                    if (daysJsonArray.length() != 0)
                    {
                        for (int index = 0; index < daysJsonArray.length(); index++)
                        {
                            daysList.add(daysJsonArray.getInt(index));
                        }
                    }
                }
                else if (response.getStatus() == CODE_UNAUTHORISED || response.getStatus() == CODE_FORBIDDEN)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
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
            }
            catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
            catch (JSONException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }

        return daysList;
    }

    /**
     * GET /cameras/{id}/recordings/snapshots/{timestamp}
     * <p/>
     * Returns the snapshot stored for this camera closest to the given timestamp
     *
     * @param cameraId  the camera's unique identifier with Evercam
     * @param timeStamp snapshot Unix timestamp
     * @param withData  whether it should send image data
     * @param range     time range in seconds around specified timestamp. Default range is one second (so it matches only exact timestamp).
     */
    public static Snapshot getByTime(String cameraId, int timeStamp, boolean withData, int range) throws EvercamException
    {
        String url = URL + "/" + cameraId + "/recordings/snapshots/" + timeStamp;
        if (withData)
        {
            url += "?with_data=true&range=" + range;
        }
        else
        {
            url += "?range=" + range;
        }
        ArrayList<Snapshot> snapshotList = getSnapshotsByUrl(url);
        if (snapshotList.size() > 0)
        {
            return snapshotList.get(0);
        }
        else
        {
            throw new EvercamException("Snapshot does not exist");
        }
    }

    /**
     * GET /cameras/{id}/recordings/snapshots/latest
     * <p/>
     * Returns latest snapshot stored for this camera.
     *
     * @param cameraId the camera's unique identifier with Evercam
     * @param withData whether it should send image data
     * @throws EvercamException
     */
    public static Snapshot getLatest(String cameraId, boolean withData) throws EvercamException
    {
        String url = URL + "/" + cameraId + "/recordings/snapshots/latest";

        if (withData)
        {
            url += "?with_data=true";
        }
        ArrayList<Snapshot> snapshotList = getSnapshotsByUrl(url);
        if (snapshotList.size() > 0)
        {
            return snapshotList.get(0);
        }
        else
        {
            throw new EvercamException("No snapshot saved for camera:" + cameraId);
        }
    }

    /**
     * Return snapshot list by request URL. This method is called by getLatest() and getByTime
     */
    private static ArrayList<Snapshot> getSnapshotsByUrl(String url) throws EvercamException
    {
        ArrayList<Snapshot> snapshotList = new ArrayList<Snapshot>();
        HttpResponse<JsonNode> response;
        if (API.hasUserKeyPair())
        {
            try
            {
                response = Unirest.get(url).queryString(API.userKeyPairMap()).header("accept", "application/json").asJson();

                if (response.getStatus() == CODE_OK)
                {
                    JSONObject snapshotsObject = response.getBody().getObject();
                    JSONArray snapshotJsonArray = snapshotsObject.getJSONArray("snapshots");

                    if (snapshotJsonArray.length() != 0)
                    {
                        for (int index = 0; index < snapshotJsonArray.length(); index++)
                        {
                            JSONObject snapshotJsonObject = snapshotJsonArray.getJSONObject(0);
                            snapshotList.add(new Snapshot(snapshotJsonObject));
                        }
                    }
                }
                else if (response.getStatus() == CODE_UNAUTHORISED || response.getStatus() == CODE_FORBIDDEN)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
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
            }
            catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
            catch (JSONException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
        return snapshotList;
    }

    /**
     * Return the pure base64 image data
     *
     * @param completeDataString the full data string with 'data:image/jpeg;base64,'
     */
    protected static String getBase64DataStringFrom(String completeDataString)
    {
        if (completeDataString != null)
        {
            return completeDataString.substring(completeDataString.indexOf(",") + 1);
        }
        return null;
    }

    /**
     * Return byte data from base64 data string
     *
     * @param base64DataString the base64 data in string format
     */
    protected static byte[] getDataFrom(String base64DataString)
    {
        if (base64DataString != null)
        {
            return org.apache.commons.codec.binary.Base64.decodeBase64(base64DataString);
        }
        return null;
    }

    public String getNotes() throws EvercamException
    {
        try
        {
            return jsonObject.getString("notes");
        }
        catch (JSONException e)
        {
            return "";
        }
    }

    public int getTimeStamp() throws EvercamException
    {
        try
        {
            return jsonObject.getInt("created_at");
        }
        catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    public String getCompleteData()
    {
        try
        {
            return jsonObject.getString("data");
        }
        catch (JSONException e)
        {
            return null;
        }
    }

    public String getBase64DataString()
    {
        String completeImageData = getCompleteData();
        return getBase64DataStringFrom(completeImageData);
    }

    public byte[] getData()
    {
        String base64Data = getBase64DataString();
        return getDataFrom(base64Data);
    }
}
