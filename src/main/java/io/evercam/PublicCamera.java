package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class PublicCamera extends EvercamObject {
    static String URL = API.URL + "public/cameras";

    private PublicCamera() {
        //private constructor
    }

    /**
     * Fetch nearest publicly discoverable camera from within the Evercam system.
     * If location isn't provided requester's IP address is used.
     *
     * @param nearTo an address or 'longitude, latitude' points. Can be null then IP address will be used
     * @return the nearest camera object with thumbnail data
     */
    public static Camera getNearest(String nearTo) throws EvercamException {
        try {
            HttpResponse<JsonNode> response;
            if (nearTo == null) {
                response = Unirest.get(URL + "/nearest").header("accept", "application/json").asJson();
            } else {
                response = Unirest.get(URL + "/nearest").queryString("near_to", nearTo).header("accept", "application/json")
                        .asJson();
            }

            if (response.getStatus() == CODE_OK) {
                JSONArray cameraArray = response.getBody().getObject().getJSONArray("cameras");
                if (cameraArray.length() > 0) {
                    JSONObject cameraJSONObject = cameraArray.getJSONObject(0);
                    return new Camera(cameraJSONObject);
                }
            } else {
                ErrorResponse errorResponse = new ErrorResponse(response.getBody().getObject());
                throw new EvercamException(errorResponse.getMessage());

            }
        } catch (UnirestException e) {
            throw new EvercamException(e);
        }
        return null;
    }

    /**
     * Returns jpg from nearest publicly discoverable camera from within the Evercam system.
     * If location isn't provided requester's IP address is used
     *
     * @param nearTo an address or 'longitude, latitude' points. Can be null then IP address will be used
     * @return the nearest camera object with thumbnail data
     */
    public static InputStream getNearestJpg(String nearTo) throws EvercamException
    {
        try
        {
            HttpResponse response;
            if(nearTo == null)
            {
                response = Unirest.get(URL + "/nearest.jpg").header("accept", "application/json").asBinary();
            }
            else
            {
                response = Unirest.get(URL + "/nearest.jpg").queryString("near_to", nearTo).header("accept",
                        "application/json").asBinary();
            }

            if (response.getStatus() == CODE_OK)
            {
                return response.getRawBody();
            }
            else
            {
                return null;
            }
        } catch (UnirestException e)
        {
            throw new EvercamException(e);
        }
    }
}
