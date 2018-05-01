package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Camera extends EvercamObject
{
    static String URL = API.URL + "cameras";

    Camera(JSONObject cameraJSONObject)
    {
        this.jsonObject = cameraJSONObject;
    }

    /**
     * Create a new camera owned by the authenticating user
     *
     * @param cameraDetail Evercam camera detail object that produced by CameraBuilder
     * @return Evercam camera object.
     * @throws EvercamException If camera is not successfully created.
     * @see CameraDetail
     * @see CameraBuilder
     */
    public static Camera create(CameraDetail cameraDetail) throws EvercamException
    {
        Camera camera = null;
        if (API.hasUserKeyPair())
        {
            try
            {
                JSONObject cameraJSONObject = buildJSONObject(cameraDetail);
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(URL);
                post.setHeader("Content-type", "application/json");
                post.setHeader("Accept", "application/json");
                post.setEntity(new StringEntity(cameraJSONObject.toString()));
                org.apache.http.HttpResponse response = client.execute(post);
                String result = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == CODE_UNAUTHORISED)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_AUTH);
                }
                else if (statusCode == CODE_ERROR)
                {
                    ErrorResponse errorResponse = new ErrorResponse(result);
                    throw new EvercamException(errorResponse.getProperErrorMessage());
                }
                else if (statusCode == CODE_CREATE)
                {
                    JsonNode jsonNode = new JsonNode(result);
                    JSONObject jsonObject = jsonNode.getObject().getJSONArray("cameras").getJSONObject(0);
                    camera = new Camera(jsonObject);
                }
                else if (statusCode == CODE_SERVER_ERROR)
                {
                    throw new EvercamException(EvercamException.MSG_SERVER_ERROR);
                }
                else if (statusCode == CODE_CONFLICT)
                {
                    ErrorResponse errorResponse = new ErrorResponse(result);
                    throw new EvercamException(errorResponse.getMessage());
                }
                else if (statusCode == CODE_APPLICATION_ERROR)
                {
                    throw new EvercamException("Application error");
                }
                else
                {
                    throw new EvercamException(statusCode + result);
                }
            } catch (JSONException e)
            {
                throw new EvercamException(e);
            } catch (ClientProtocolException e)
            {
                throw new EvercamException(e);
            } catch (IOException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
        return camera;
    }

    /**
     * Delete a camera from Evercam along with any stored media.
     *
     * @param cameraId the unique identifier of the camera
     * @return If the camera delete is successful, return true, otherwise return false.
     * @throws EvercamException If camera not exists or user unauthorized
     */
    public static boolean delete(String cameraId) throws EvercamException
    {
        if (API.hasUserKeyPair())
        {
            try
            {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpDelete delete = new HttpDelete(URL + '/' + cameraId + '/' + "?api_key=" + API.getUserKeyPair()[0] + "&api_id=" + API.getUserKeyPair()[1]);
                delete.setHeader("Content-type", "application/json");
                delete.setHeader("Accept", "application/json");
                org.apache.http.HttpResponse response = client.execute(delete);
                String result = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == CODE_OK)
                {
                    return true;
                }
                else if (statusCode == CODE_UNAUTHORISED)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_USER_KEY);
                }
                else if (statusCode == CODE_NOT_FOUND)
                {
                    throw new EvercamException(result);
                }
                else if (statusCode == CODE_SERVER_ERROR)
                {
                    throw new EvercamException(EvercamException.MSG_SERVER_ERROR);
                }
                else
                {
                    return false;
                }
            } catch (ClientProtocolException e)
            {
                throw new EvercamException(e);
            } catch (IOException e)
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
     * Updates full or partial data for an existing camera
     *
     * @param cameraDetail Evercam camera detail object that produced by PatchCameraBuilder
     * @return Evercam camera object.
     * @throws EvercamException If user unauthorized or error occurred with Evercam
     * @see CameraDetail
     * @see PatchCameraBuilder
     */
    public static Camera patch(CameraDetail cameraDetail) throws EvercamException
    {
        Camera camera = null;
        if (API.hasUserKeyPair())
        {
            try
            {
                JSONObject cameraJSONObject = buildJSONObject(cameraDetail);
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPatch patch = new HttpPatch(URL + '/' + cameraDetail.id);
                patch.setHeader("Content-type", "application/json");
                patch.setHeader("Accept", "application/json");
                patch.setEntity(new StringEntity(cameraJSONObject.toString()));
                org.apache.http.HttpResponse response = client.execute(patch);
                String result = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == CODE_UNAUTHORISED || statusCode == CODE_FORBIDDEN)
                {
                    throw new EvercamException(EvercamException.MSG_INVALID_AUTH);
                }
                else if (statusCode == CODE_ERROR)
                {
                    ErrorResponse errorResponse = new ErrorResponse(result);
                    String message = errorResponse.getMessage();
                    throw new EvercamException(message);
                }
                else if (statusCode == CODE_OK)
                {
                    JsonNode jsonNode = new JsonNode(result);
                    JSONObject jsonObject = jsonNode.getObject().getJSONArray("cameras").getJSONObject(0);
                    camera = new Camera(jsonObject);
                }
                else if (statusCode == CODE_SERVER_ERROR)
                {
                    throw new EvercamException(EvercamException.MSG_SERVER_ERROR);
                }
                else
                {
                    throw new EvercamException(statusCode + " " + result);
                }
            } catch (JSONException e)
            {
                throw new EvercamException(e);
            } catch (ClientProtocolException e)
            {
                throw new EvercamException(e);
            } catch (IOException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
        return camera;
    }

    /**
     * Fetch details of a camera from Evercam by camera unique identifier
     *
     * @param cameraId         the camera's unique identifier with Evercam
     * @param includeThumbnail set to true to get base64 encoded 150x150 thumbnail with camera view
     * @return Evercam camera object with all data of this camera
     * @throws EvercamException If user unauthorized or error occurred with Evercam
     */
    public static Camera getById(String cameraId, boolean includeThumbnail) throws EvercamException
    {
        String url = URL + '/' + cameraId;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("thumbnail", Boolean.toString(includeThumbnail));
        ArrayList<Camera> cameraArrayList = getByUrl(url, map);
        return cameraArrayList.isEmpty() ? null : cameraArrayList.get(0);
    }

    /**
     * Returns the set of cameras associated with given conditions
     * API key pair has to be specified before calling this method
     *
     * @param userId           unique Evercam username of the user, can be null
     * @param includeShared    whether or not to include cameras shared with the user in the fetch.
     * @param includeThumbnail whether or not to get base64 encoded 150x150 thumbnail with camera view for each camera
     * @return the camera list that associated with the specified user
     * @throws EvercamException
     */
    public static ArrayList<Camera> getAll(String userId, boolean includeShared, boolean includeThumbnail)  throws EvercamException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("include_shared", Boolean.toString(includeShared));
        map.put("thumbnail", Boolean.toString(includeThumbnail));
        if (userId != null)
        {
            map.put("user_id", userId);
        }

        return getByUrl(URL, map);
    }

    /**
     * Returns data for a specified set of cameras.
     *
     * @param idSetString comma separated list of camera identifiers for the cameras being queried.
     * @return the list of specified set of cameras
     * @throws EvercamException If user unauthorized or error occurred with Evercam
     */
    public static ArrayList<Camera> getByIdSet(String idSetString) throws EvercamException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", idSetString);
        return getByUrl(URL, map);
    }

    /**
     * Request camera live snapshot from Evercam.
     *
     * @param cameraId the camera's unique identifier
     * @return the stream of camera live image
     * @throws EvercamException
     */
    public static InputStream getSnapshotByCameraId(String cameraId) throws EvercamException
    {
        InputStream inputStream;
        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse response = Unirest.get(URL + "/" + cameraId + "/live/snapshot.jpg").queryString(API.userKeyPairMap())
                        .asBinary();
                inputStream = response.getRawBody();
            } catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
        return inputStream;
    }

    /**
     * PUT /cameras/{id}
     * Transfers the ownership of a camera from one user to another
     *
     * @param cameraId The unique identifier for the camera.
     * @param userId The Evercam user name or email address for the new camera owner.
     * @throws EvercamException if user key and id not specified
     */
    public static Camera transfer(String cameraId, String userId) throws EvercamException
    {
        Camera transferredCamera;

        if (API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.put(URL + '/' + cameraId)
                        .queryString("user_id", userId).queryString(API.userKeyPairMap()).asJson();
                JSONObject object = response.getBody().getObject();
                if (response.getStatus() == CODE_OK)
                {
                    transferredCamera = new Camera(object.getJSONArray("cameras").getJSONObject(0));
                }
                else
                {
                    ErrorResponse errorResponse = new ErrorResponse(object);
                    String message = errorResponse.getMessage();
                    throw new EvercamException(message);
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

        return transferredCamera;
    }

    /**
     * Check if camera details contain username and password or not.
     *
     * @return True if username and password exists in camera model, otherwise return False.
     */
    public boolean hasCredentials()
    {
        //FIXME: Should be replace by a better logic, for example by using 'owned' and 'rights' to tell the camera has credential parameters or not.
        return jsonObject.toString().contains("\"cam_username\":") && jsonObject.toString().contains("\"cam_password\":");
    }

    /**
     * Return location object of this camera.
     * Return null if no location data associate with this camera.
     *
     * @see io.evercam.Location
     */
    public Location getLocation() throws EvercamException
    {
        if (jsonObject.isNull("location"))
        {
            return null;
        }
        else
        {
            JSONObject locationJsonObject = getJsonObjectByString("location");
            return new Location(locationJsonObject);
        }
    }

    /**
     * Return external host name of the camera, return an empty string if
     * the host name does not exist.
     *
     * @return external host name of the camera
     * @throws EvercamException
     */
    public String getExternalHost() throws EvercamException
    {
        External externalObject = getExternalObject();
        if (externalObject != null)
        {
            return externalObject.getHost();
        }
        return "";
    }

    /**
     * Return internal host name of the camera, return an empty string if
     * the host name does not exist.
     *
     * @return internal host name of the camera
     * @throws EvercamException
     */
    public String getInternalHost() throws EvercamException
    {
        Internal internalObject = getInternalObject();
        if (internalObject != null)
        {
            return internalObject.getHost();
        }
        return "";
    }

    /**
     * Return external HTTP port number of the camera,
     * return 0 if no internal HTTP port associated with this camera.
     *
     * @return external HTTP port of the camera
     * @throws EvercamException
     */
    public int getExternalHttpPort() throws EvercamException
    {
        External externalObject = getExternalObject();
        if (externalObject != null)
        {
            return externalObject.getHttp().getPort();
        }
        return 0;
    }

    /**
     * Return internal HTTP port number of the camera,
     * return 0 if no internal HTTP port associated with this camera.
     *
     * @return internal HTTP port of the camera
     * @throws EvercamException
     */
    public int getInternalHttpPort() throws EvercamException
    {
        Internal internalObject = getInternalObject();
        if (internalObject != null)
        {
            return internalObject.getHttp().getPort();
        }
        return 0;
    }

    /**
     * Return external RTSP port number of the camera,
     * return 0 if no external RTSP port associated with this camera.
     *
     * @return external RTSP port of the camera
     * @throws EvercamException
     */
    public int getExternalRtspPort() throws EvercamException
    {
        External externalObject = getExternalObject();
        if (externalObject != null)
        {
            return externalObject.getRtsp().getPort();
        }
        return 0;
    }

    /**
     * Return internal RTSP port number of the camera,
     * return 0 if no internal RTSP port associated with this camera.
     *
     * @return internal RTSP port of the camera
     * @throws EvercamException
     */
    public int getInternalRtspPort() throws EvercamException
    {
        Internal internalObject = getInternalObject();
        if (internalObject != null)
        {
            return internalObject.getRtsp().getPort();
        }
        return 0;
    }

    /**
     * Return camera's username. If no username associated with the camera,
     * return an empty string.
     */
    public String getUsername()
    {
        return getStringNotNull("cam_username");
    }

    /**
     * Return camera's password. If no password associated with the camera,
     * return an empty string.
     */
    public String getPassword()
    {
        return getStringNotNull("cam_password");
    }

    /**
     * Return unique Evercam identifier for the camera.
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
     * Return username of camera owner.
     *
     * @throws EvercamException
     */
    public String getOwner() throws EvercamException
    {
        return getStringNotNull("owner");
    }

    /**
     * Whether or not this camera is publically available.
     *
     * @return True if this camera is publically available, otherwise return false.
     * @throws EvercamException
     */
    public boolean isPublic() throws EvercamException
    {
        try
        {
            return jsonObject.getBoolean("is_public");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Whether the camera is publicly findable.
     *
     * @return True if the camera is publicly findable, otherwise return false.
     * @throws EvercamException
     */
    public boolean isDiscoverable() throws EvercamException
    {
        try
        {
            return jsonObject.getBoolean("discoverable");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return human readable or friendly name for the camera.
     *
     * @throws EvercamException
     */
    public String getName() throws EvercamException
    {
        try
        {
            return jsonObject.getString("name");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return unique identifier for the camera vendor.
     *
     * @throws EvercamException
     */
    public String getVendorId() throws EvercamException
    {
        return getStringNotNull("vendor_id");
    }

    /**
     * Return the name for the camera vendor.
     *
     * @throws EvercamException
     */
    public String getVendorName() throws EvercamException
    {
        return getStringNotNull("vendor_name");
    }

    /**
     * Return name of the IANA/tz timezone where this camera is located.
     *
     * @throws EvercamException
     */
    public String getTimezone() throws EvercamException
    {
        try
        {
            return jsonObject.getString("timezone");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return the name of the camera model.
     *
     * @throws EvercamException
     */
    public String getModelName() throws EvercamException
    {
        try
        {
            return jsonObject.getString("model_name");
        } catch (JSONException e)
        {
            return "";
        }
    }

    /**
     * Return the unique identifier of the camera model.
     *
     * @throws EvercamException
     */
    public String getModelId() throws EvercamException
    {
        return getStringNotNull("model_id");
    }

    /**
     * Return the physical network MAC address of the camera, return an
     * empty string if no MAC address associated with this camera.
     */
    public String getMacAddress()
    {
        return getStringNotNull("mac_address");
    }

    /**
     * Whether or not this camera is currently online.
     *
     * @return True if this camera is currently online, otherwise return false.
     */
    public boolean isOnline()
    {
        try
        {
            return jsonObject.getBoolean("is_online");
        } catch (JSONException e)
        {
            //Return false instead of throw a new exception
            //Because the exception occurs when online status is 'null'.
            return false;
        }
    }

    /**
     * Whether or not this camera is owned by the authenticated user.
     *
     * @return True if this camera is owned by this user, otherwise return false.
     * @throws EvercamException
     */
    public boolean isOwned() throws EvercamException
    {
        try
        {
            return jsonObject.getBoolean("owned");
        } catch (JSONException e)
        {
            throw new EvercamException(e);
        }
    }

    /**
     * Return internal web page URL for this camera.
     * Return an empty string if no internal url associated with this camera.
     */
    public String getInternalCameraEndpoint() throws EvercamException
    {
        Internal internalObject = getInternalObject();
        if (internalObject != null)
        {
            return internalObject.getHttp().getCameraUrl();
        }
        return "";
    }

    /**
     * Return external web page URL for this camera.
     * Return an empty string if no external url associated with this camera.
     */
    public String getExternalCameraEndpoint() throws EvercamException
    {
        External externalObject = getExternalObject();
        if (externalObject != null)
        {
            return externalObject.getHttp().getCameraUrl();
        }
        return "";
    }

    /**
     * Return internal full jpg URL of this camera that can be
     * used to request a camera live image. Return an empty string
     * if the URL does not exist.
     *
     * @return full internal snapshot URL (jpg) of this camera.
     * @throws EvercamException
     */
    public String getInternalJpgUrl() throws EvercamException
    {
        Internal internalObject = getInternalObject();
        if (internalObject != null)
        {
            return internalObject.getHttp().getJpgUrl();
        }
        return "";
    }

    /**
     * Return external full jpg URL of this camera that can be
     * used to request a camera live image. Return an empty string
     * if the URL does not exist.
     *
     * @return full external snapshot URL (jpg) of this camera.
     * @throws EvercamException
     */
    public String getExternalJpgUrl() throws EvercamException
    {
        External externalObject = getExternalObject();
        if (externalObject != null)
        {
            return externalObject.getHttp().getJpgUrl();
        }
        return "";
    }

    /**
     * Return internal RTSP stream (H264) URL of this camera for
     * video playing. Return an empty string if the URL does not exist.
     *
     * @return full internal stream URL (h264) of this camera.
     * @throws EvercamException
     */
    public String getInternalH264Url() throws EvercamException
    {
        Internal internalObject = getInternalObject();
        if (internalObject != null)
        {
            return internalObject.getRtsp().getH264Url();
        }
        return "";
    }

    /**
     * Return internal RTSP stream (H264) URL of this camera for
     * video playing. Return an empty string if the URL does not exist.
     *
     * @return full internal stream URL (h264) of this camera.
     * @throws EvercamException
     */
    public String getExternalH264Url() throws EvercamException
    {
        External externalObject = getExternalObject();
        if (externalObject != null)
        {
            return externalObject.getRtsp().getH264Url();
        }
        return "";
    }

    /**
     * Return the internal full stream URL (H264) with username and password
     * for the basic authentication of media player.
     *
     * @return the H264 URL with credentials.
     * @throws EvercamException
     */
    public String getInternalH264UrlWithCredential() throws EvercamException
    {
        return replaceUrlWithCredential(getInternalH264Url(), RTSP_PREFIX);
    }

    /**
     * Return the external full stream URL (H264) with username and password
     * for the basic authentication of media player.
     *
     * @return the H264 URL with credentials.
     * @throws EvercamException
     */
    public String getExternalH264UrlWithCredential() throws EvercamException
    {
        return replaceUrlWithCredential(getExternalH264Url(), RTSP_PREFIX);
    }

    /**
     * @param url    the full URL without credentials.
     * @param prefix the prefix of the URL.
     * @return the full URL with credentials as prefix://username:password@host
     * @throws EvercamException
     */
    private String replaceUrlWithCredential(String url, String prefix) throws EvercamException
    {
        if (!url.isEmpty() && url.startsWith(prefix))
        {
            return url.replace(prefix, prefix + getUsername() + ":" + getPassword() + "@");
        }
        else
        {
            return "";
        }
    }

    /**
     * Request for response with a specified URL and credentials for basic authentication.
     * Return as InputStream.
     *
     * @param url      snapshot URL of this camera
     * @param username username for this camera
     * @param password password for this camera
     * @throws EvercamException If error happen with the HTTP request.
     */
    public static InputStream getStreamFromUrl(String url, String username, String password) throws EvercamException
    {
        InputStream inputStream;
        try
        {
            HttpResponse response = Unirest.get(url).basicAuth(username, password).asBinary();
            inputStream = response.getRawBody();
        } catch (UnirestException e)
        {
            throw new EvercamException(e);
        }
        return inputStream;
    }

    /**
     * Request camera live snapshot from Evercam.
     * Equivalent with static method Camera.getSnapshotByCameraId(String),
     * with camera identifier auto filled for this camera object.
     *
     * @return the stream of camera live image
     * @throws EvercamException
     */
    public InputStream getSnapshotFromEvercam() throws EvercamException
    {
        return getSnapshotByCameraId(getId());
    }

    /**
     * Validate the specified URL in valid or not by send a HTTP request.
     *
     * @param url the URL that need to be validated.
     * @return True if get a valid response, otherwise return false.
     */
    private boolean isValidUrl(String url)
    {
        try
        {
            HttpResponse response = Unirest.get(url).asBinary();
            if (response.getStatus() != CODE_ERROR)
            {
                return true;
            }
        } catch (UnirestException e)
        {
            //do nothing
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Return The array list that contains internal and/or external endpoint URL
     * of this camera (http://host:port).
     */
    public ArrayList<String> getEndpoints()
    {
        ArrayList<String> endpointsArray = new ArrayList<String>();
        try
        {
            if (getInternalHost() != null && !getInternalHost().equals("null"))
            {
                String internalUrl = HTTP_PREFIX + getInternalHost() + ":" + getInternalHttpPort();
                endpointsArray.add(internalUrl);
            }
            if (getExternalHost() != null && !getExternalHost().equals("null"))
            {
                String externalUrl = HTTP_PREFIX + getExternalHost() + ":" + getExternalHttpPort();
                endpointsArray.add(externalUrl);
            }
        } catch (EvercamException e)
        {
            e.printStackTrace();
        }
        return endpointsArray;
    }

    /**
     * Build camera's JSON object for API requests.
     *
     * @param cameraDetail the produced camera detail object
     * @return a JSON object with all details for the camera
     * @throws JSONException
     */
    private static JSONObject buildJSONObject(CameraDetail cameraDetail) throws JSONException
    {
        JSONObject cameraJSONObject = new JSONObject();
        cameraJSONObject.put("api_key", API.getUserKeyPair()[0]);
        cameraJSONObject.put("api_id", API.getUserKeyPair()[1]);
        cameraJSONObject.put("id", cameraDetail.id);
        if (cameraDetail.internalHost != null)
        {
            cameraJSONObject.put("internal_host", cameraDetail.internalHost);
        }
        if (cameraDetail.internalHttpPort != 0)
        {
            cameraJSONObject.put("internal_http_port", cameraDetail.internalHttpPort);
        }
        if (cameraDetail.internalRtspPort != 0)
        {
            cameraJSONObject.put("internal_rtsp_port", cameraDetail.internalRtspPort);
        }
        if (cameraDetail.externalHost != null)
        {
            cameraJSONObject.put("external_host", cameraDetail.externalHost);
        }
        if (cameraDetail.externalHttpPort != 0)
        {
            cameraJSONObject.put("external_http_port", cameraDetail.externalHttpPort);
        }
        if (cameraDetail.externalRtspPort != 0)
        {
            cameraJSONObject.put("external_rtsp_port", cameraDetail.externalRtspPort);
        }
        if (cameraDetail.jpgUrl != null)
        {
            cameraJSONObject.put("jpg_url", cameraDetail.jpgUrl);
        }
        if (cameraDetail.mjpgUrl != null)
        {
            cameraJSONObject.put("mjpg_url", cameraDetail.mjpgUrl);
        }
        if (cameraDetail.mpegUrl != null)
        {
            cameraJSONObject.put("mpeg_url", cameraDetail.mpegUrl);
        }
        if (cameraDetail.h264Url != null)
        {
            cameraJSONObject.put("h264_url", cameraDetail.h264Url);
        }
        if (cameraDetail.audioUrl != null)
        {
            cameraJSONObject.put("audio_url", cameraDetail.audioUrl);
        }
        if (cameraDetail.isPublic != null)
        {
            cameraJSONObject.put("is_public", cameraDetail.isPublic);
        }
        if (cameraDetail.isOnline != null)
        {
            cameraJSONObject.put("is_online", cameraDetail.isOnline);
        }
        if (cameraDetail.cameraUsername != null)
        {
            cameraJSONObject.put("cam_username", cameraDetail.cameraUsername);
        }
        if (cameraDetail.cameraPassword != null)
        {
            cameraJSONObject.put("cam_password", cameraDetail.cameraPassword);
        }
        if (cameraDetail.name != null)
        {
            cameraJSONObject.put("name", cameraDetail.name);
        }
        if (cameraDetail.model != null)
        {
            cameraJSONObject.put("model", cameraDetail.model);
        }
        if (cameraDetail.vendor != null)
        {
            cameraJSONObject.put("vendor", cameraDetail.vendor);
        }
        if (cameraDetail.timezone != null)
        {
            cameraJSONObject.put("timezone", cameraDetail.timezone);
        }
        if (cameraDetail.macAddress != null)
        {
            cameraJSONObject.put("mac_address", cameraDetail.macAddress);
        }
        if (cameraDetail.locationLat != null)
        {
            cameraJSONObject.put("location_lat", cameraDetail.locationLat);
        }
        if (cameraDetail.locationLng != null)
        {
            cameraJSONObject.put("location_lng", cameraDetail.locationLng);
        }
        if (cameraDetail.locationLatString != null)
        {
            cameraJSONObject.put("location_lat", cameraDetail.locationLatString);
        }
        if (cameraDetail.locationLngString != null)
        {
            cameraJSONObject.put("location_lng", cameraDetail.locationLngString);
        }
        if(cameraDetail.isDiscoverable != null)
        {
            cameraJSONObject.put("discoverable", cameraDetail.isDiscoverable);
        }
        return cameraJSONObject;
    }

    /**
     * Return the 'internal' object of this camera.
     * Return null if it's a shared camera with no internal details.
     */
    public Internal getInternalObject() throws EvercamException
    {
        try
        {
            JSONObject internalJsonObject = getJsonObjectByString("internal");
            return new Internal(internalJsonObject);
        } catch (EvercamException e)
        {
            return null;
        }
    }

    /**
     * Return the 'external' object of this camera.
     * Return null if it's a shared camera with no external details.
     */
    public External getExternalObject()
    {
        try
        {
            JSONObject externalJsonObject = getJsonObjectByString("external");
            return new External(externalJsonObject);
        } catch (EvercamException e)
        {
            return null;
        }
    }

    /**
     * Return the dynamic DNS object of this camera.
     */
    public ProxyUrl getProxyUrl()
    {
        try
        {
            JSONObject proxyUrlJsonObject = getJsonObjectByString("proxy_url");
            return new ProxyUrl(proxyUrlJsonObject);
        } catch (EvercamException e)
        {
            return null;
        }
    }

    /**
     * Return the authenticated user's rights on this camera
     *
     * @throws EvercamException if no rights associated with the camera
     */
    public Right getRights() throws EvercamException
    {
        try
        {
            String rightsString = jsonObject.getString("rights");
            return new Right(rightsString);
        } catch (JSONException e)
        {
            throw new EvercamException("No rights associated with this camera.");
        }
    }

    /**
     * Return byte thumbnail(150x150 preview of camera view) data associated with this camera.
     *
     * @throws EvercamException if no thumbnails associated with this camera
     */
    public byte[] getThumbnailData() throws EvercamException
    {
        try
        {
            String thumbnailString = jsonObject.getString("thumbnail");
            String base64ImageString = Snapshot.getBase64DataStringFrom(thumbnailString);
            return Snapshot.getDataFrom(base64ImageString);
        } catch (JSONException e)
        {
            throw new EvercamException("No thumbnails associated with this camera.");
        }
    }

    /**
     * Return a URL of latest snapshot thumbnail
     */
    public String getThumbnailUrl()
    {
        return jsonObject.getString("thumbnail_url");
    }

    /**
     * Get camera list by requesting a specified URL and parameters as a map
     */
    private static ArrayList<Camera> getByUrl(String url, Map<String, Object> parameterMap) throws EvercamException
    {
        ArrayList<Camera> cameraList = new ArrayList<Camera>();

        if(API.hasUserKeyPair())
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get(url).queryString(API.userKeyPairMap()).queryString(parameterMap)
                        .header("accept", "application/json").asJson();

                if (response.getStatus() == CODE_OK)
                {
                    JSONArray camerasJSONArray = response.getBody().getObject().getJSONArray("cameras");
                    for (int count = 0; count < camerasJSONArray.length(); count++)
                    {
                        JSONObject cameraJSONObject = camerasJSONArray.getJSONObject(count);
                        cameraList.add(new Camera(cameraJSONObject));
                    }
                }
                else
                {
                    throw new EvercamException(response.getBody().toString());
                }
            } catch (JSONException e)
            {
                throw new EvercamException(e);
            } catch (UnirestException e)
            {
                throw new EvercamException(e);
            }
        }
        else
        {
            throw new EvercamException(EvercamException.MSG_USER_API_KEY_REQUIRED);
        }
        return cameraList;
    }

    /**
     * Tests if given camera parameters are correct
     *
     * @param externalUrl External camera URL, in format http://ip:port
     * @param jpgUrl The snapshot URL ending
     * @param cameraUsername camera's username
     * @param cameraPassword camera's password
     * @return Return the snapshot if the snapshot is available, otherwise return null
     * @throws EvercamException
     */
    public static Snapshot testSnapshot(String externalUrl, String jpgUrl, String cameraUsername, String
            cameraPassword) throws EvercamException
    {
        try
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("external_url", externalUrl);
            map.put("jpg_url", jpgUrl);
            map.put("cam_username", cameraUsername);
            map.put("cam_password", cameraPassword);

            HttpResponse<JsonNode> httpResponse = Unirest.post(URL + "/test").fields(map).asJson();
            int statusCode = httpResponse.getStatus();
            if(statusCode == CODE_OK)
            {
                return new Snapshot(httpResponse.getBody().getObject());
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
        return null;
    }
}
