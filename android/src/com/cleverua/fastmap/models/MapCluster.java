package com.cleverua.fastmap.models;

import org.json.JSONObject;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 10:50 AM
 * Email: akulakovsky@cleverua.com
 */
public class MapCluster {

    public static final String KEY_LATITUDE = "lat";
    public static final String KEY_LONGTITUDE = "lng";
    public static final String KEY_COUNT = "cost";
    public static final String KEY_MIN_LAT = "min_lat";
    public static final String KEY_MIN_LNG = "min_lng";
    public static final String KEY_MAX_LAT = "max_lat";
    public static final String KEY_MAX_LNG = "max_lng";

    private Double lat;
    private Double lng;
    private Double minLat;
    private Double minLng;
    private Double maxLat;
    private Double maxLng;
    private int count;

    public MapCluster(Double lat, Double lng, Double minLat, Double minLng, Double maxLat, Double maxLng, int count) {
        this.lat = lat;
        this.lng = lng;
        this.minLat = minLat;
        this.minLng = minLng;
        this.maxLat = maxLat;
        this.maxLng = maxLng;
        this.count = count;
    }

    public static MapCluster parse(JSONObject json) {

        Double lat = null;
        Double lng = null;
        Double minLat = null;
        Double minLng = null;
        Double maxLat = null;
        Double maxLng = null;
        int count = 0;

        if (!json.isNull(KEY_LATITUDE)) {
            lat = json.optDouble(KEY_LATITUDE);
        }

        if (!json.isNull(KEY_LONGTITUDE)) {
            lng = json.optDouble(KEY_LONGTITUDE);
        }

        if (!json.isNull(KEY_COUNT)) {
            count = json.optInt(KEY_COUNT);
        }

        if (!json.isNull(KEY_MIN_LAT)) {
            minLat = json.optDouble(KEY_MIN_LAT);
        }

        if (!json.isNull(KEY_MIN_LNG)) {
            minLng = json.optDouble(KEY_MIN_LNG);
        }

        if (!json.isNull(KEY_MAX_LAT)) {
            maxLat = json.optDouble(KEY_MAX_LAT);
        }

        if (!json.isNull(KEY_MAX_LNG)) {
            maxLng = json.optDouble(KEY_MAX_LNG);
        }

        return new MapCluster(lat, lng, minLat, minLng, maxLat, maxLng, count);
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public int getCount() {
        return count;
    }

    public Double getMinLat() {
        return minLat;
    }

    public Double getMinLng() {
        return minLng;
    }

    public Double getMaxLat() {
        return maxLat;
    }

    public Double getMaxLng() {
        return maxLng;
    }

}
