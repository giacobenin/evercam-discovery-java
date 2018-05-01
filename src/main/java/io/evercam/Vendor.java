package io.evercam;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Vendor extends EvercamObject {
    private static String URL_VENDORS = API.URL + "vendors";

    private JSONObject jsonObject;

    Vendor(JSONObject vendorJSONObject) {
        this.jsonObject = vendorJSONObject;
    }

    /**
     * Search for a camera vendor by unique identifier
     *
     * @param vendorId the vendor's unique identifier with Evercam
     * @return the vendor that match this unique identifier
     * @throws EvercamException if develop key and id is not specified, or vendor not found
     */
    public static Vendor getById(String vendorId) throws EvercamException {
        ArrayList<Vendor> vendors = getVendors(URL_VENDORS + '/' + vendorId);
        if (vendors.size() > 0) {
            return vendors.get(0);
        } else {
            throw new EvercamException("Vendor with id " + vendorId + " not exists");
        }
    }

    public static ArrayList<Vendor> getAll() throws EvercamException {
        return getVendors(URL_VENDORS);
    }

    public static ArrayList<Vendor> getByMac(String mac) throws EvercamException {
        return getVendors(URL_VENDORS + "?mac=" + mac);
    }

    public static ArrayList<Vendor> getByName(String name) throws EvercamException {
        return getVendors(URL_VENDORS + "?name=" + name);
    }

    public String getId() throws EvercamException {
        try {
            return jsonObject.getString("id");
        } catch (JSONException e) {
            throw new EvercamException(e);
        }
    }

    public String getName() throws EvercamException {
        try {
            return jsonObject.getString("name");
        } catch (JSONException e) {
            throw new EvercamException(e);
        }
    }

    public ArrayList<String> getKnownMacs() throws EvercamException {
        ArrayList<String> knownMacs = new ArrayList<String>();
        try {
            JSONArray knownMacJSONArray = jsonObject.getJSONArray("known_macs");
            for (int arrayIndex = 0; arrayIndex < knownMacJSONArray.length(); arrayIndex++) {
                knownMacs.add(arrayIndex, knownMacJSONArray.getString(arrayIndex));
            }
        } catch (JSONException e) {
            throw new EvercamException(e);
        }
        return knownMacs;
    }

    /**
     * @return Logo thumbnail URL for this camera vendor
     * @throws EvercamException if logo doesn't exist in vendor object
     */
    public String getLogoUrl() throws EvercamException {
        try {
            return jsonObject.getString("logo");
        } catch (JSONException e) {
            throw new EvercamException(e);
        }
    }

    /**
     * @return Return the logo URL based on the specified vendor ID
     */
    public static String getLogoUrl(String vendorId) {
        return API.AWS_ASSETS_URL + vendorId + "/logo.jpg";
    }

    public ArrayList<Model> getAllModels() throws EvercamException {
        return Model.getAllByVendorId(getId());
    }

    public Model getDefaultModel() throws EvercamException {
        return Model.getById(getId() + Model.DEFAULT_MODEL_SUFFIX);
    }

    private static ArrayList<Vendor> getVendors(String url) throws EvercamException {
        ArrayList<Vendor> vendorList = new ArrayList<Vendor>();
        HttpRequest request = Unirest.get(url);

        try {
            HttpResponse<JsonNode> response = request.header("accept", "application/json").asJson();
            if (response.getStatus() == CODE_OK) {
                JSONArray vendorsJSONArray = response.getBody().getObject().getJSONArray("vendors");
                for (int vendorIndex = 0; vendorIndex < vendorsJSONArray.length(); vendorIndex++) {
                    JSONObject vendorJSONObject = vendorsJSONArray.getJSONObject(vendorIndex);
                    vendorList.add(new Vendor(vendorJSONObject));
                }
            } else if (response.getStatus() == CODE_SERVER_ERROR) {
                throw new EvercamException(EvercamException.MSG_SERVER_ERROR);
            } else {
                throw new EvercamException(response.getBody().toString());
            }

        } catch (UnirestException e) {
            throw new EvercamException(e);
        } catch (JSONException e) {
            throw new EvercamException(e);
        }

        return vendorList;
    }
}
