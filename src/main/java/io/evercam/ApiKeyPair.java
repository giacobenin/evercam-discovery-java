package io.evercam;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiKeyPair {
    private JSONObject jsonObject;

    ApiKeyPair(JSONObject keyPairJSONObject) {
        jsonObject = keyPairJSONObject;
    }

    public String getApiKey() throws EvercamException {
        try {
            return jsonObject.getString("api_key");
        } catch (JSONException e) {
            throw new EvercamException(e);
        }
    }

    public String getApiId() throws EvercamException {
        try {
            return jsonObject.getString("api_id");
        } catch (JSONException e) {
            throw new EvercamException(e);
        }
    }
}
