package io.evercam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SnapshotsWithPaging extends EvercamObject
{
    protected SnapshotsWithPaging(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    /**
     * Return the model list, it will be an empty list if no model exists.
     */
    public ArrayList<Snapshot> getSnapshotsList()
    {
        ArrayList<Snapshot> snapshotList = new ArrayList<Snapshot>();
        JSONArray snapshotJsonArray = jsonObject.getJSONArray("snapshots");
        if (snapshotJsonArray.length() > 0)
        {
            for (int index = 0; index < snapshotJsonArray.length(); index++)
            {
                JSONObject snapshotJsonObject = snapshotJsonArray.getJSONObject(index);
                snapshotList.add(new Snapshot(snapshotJsonObject));
            }
        }
        return snapshotList;
    }

    /**
     * Return the total number of pages.
     */
    public int getTotalPages()
    {
        return jsonObject.getInt("pages");
    }
}
