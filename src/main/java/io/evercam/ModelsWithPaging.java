package io.evercam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ModelsWithPaging extends EvercamObject {
    protected ModelsWithPaging(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Return the model list, it will be an empty list if no model exists.
     */
    public ArrayList<Model> getModelsList() {
        ArrayList<Model> modelList = new ArrayList<Model>();
        JSONArray modelJsonArray = jsonObject.getJSONArray("models");
        if (modelJsonArray.length() > 0) {
            for (int index = 0; index < modelJsonArray.length(); index++) {
                Model model = new Model(modelJsonArray.getJSONObject(index));
                modelList.add(model);
            }
        }
        return modelList;
    }

    /**
     * Return the total number of pages.
     */
    public int getTotalPages() {
        return jsonObject.getInt("pages");
    }
}
