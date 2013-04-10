package com.cleverua.fastmap.models;

import android.content.ContentValues;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 11:45 AM
 * Email: akulakovsky@cleverua.com
 */
public class Model {

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";

    private int id;
    private String title;
    private Double lat;
    private Double lng;

    public Model(int id, String title, Double lat, Double lng) {
        this.id = id;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
    }

    public static Model parse(JSONObject json) throws JSONException {

        int id = -1;
        String title = null;
        Double lat = null;
        Double lng = null;

        if (!json.isNull(KEY_ID)) {
            id = json.getInt(KEY_ID);
        }

        if (!json.isNull(KEY_TITLE)) {
            title = json.getString(KEY_TITLE);
        }

        if (!json.isNull(KEY_LAT)) {
            lat = json.getDouble(KEY_LAT);
        }

        if (!json.isNull(KEY_LNG)) {
            lng = json.getDouble(KEY_LNG);
        }

        return new Model(id, title, lat, lng);
    }

    public static ContentValues createContentValues(Model model) {
        ContentValues values = new ContentValues();

        values.put("_" + KEY_ID, model.getId());
        values.put(KEY_TITLE, model.getTitle());
        values.put(KEY_LAT, model.getLat());
        values.put(KEY_LNG, model.getLng());

        return values;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
