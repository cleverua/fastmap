package com.cleverua.fastmap.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.cleverua.fastmap.helpers.DBHelper;
import com.cleverua.fastmap.interfaces.IOnMapDataDownloaded;
import com.cleverua.fastmap.models.MapCluster;
import com.cleverua.fastmap.models.Model;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 11:50 AM
 * Email: akulakovsky@cleverua.com
 */
public class GetMapDataTask extends AsyncTask<Void, GetMapDataTask.DataContainer, Void> {

    private static final String REST_URL = "";

    private Context context;
    private List<String> qTreeIndexes;

    public GetMapDataTask(Context context, List<String> qTreeIndexes) {
        this.context = context;
        this.qTreeIndexes = qTreeIndexes;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (String query: qTreeIndexes){

            if (isCancelled()){
                Log.d("NewMapActivity", "Canceling task!");
                return null;
            }

            DataContainer container = new DataContainer();
            List<ContentValues> valuesList = new ArrayList<ContentValues>();
            try {
                Log.d("NewMapActivity", "Executing query = " + query);

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(REST_URL + "/maps/" + query + ".json");
                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();
                int        statusCode     = responseStatus != null ? responseStatus.getStatusCode() : 0;

                if (statusCode == 200 && responseEntity != null){
                    String result = EntityUtils.toString(responseEntity);
                    JSONObject json = new JSONObject(result);

                    if (!json.isNull("content")){
                        JSONArray array = json.getJSONArray("content");
                        final int size = array.length();

                        for (int i = 0; i < size; i++) {
                            JSONObject item = array.getJSONObject(i);
                            String type = item.getString("type");
                            if (type.equals("group")){
                                container.clusters.add(MapCluster.parse(item));
                            } else {
                                try {
                                    Model date = Model.parse(item);
                                    container.dates.add(date);
                                    valuesList.add(Model.createContentValues(date));
                                } catch (JSONException e) {
                                    Log.d(getClass().getSimpleName(), "onPostExecute: failed to parse UserData for " + item, e);
                                }
                            }
                        }
                    }

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (valuesList.size() != 0){
                DBHelper.insertToDB(context, DBHelper.TABLE_MODEL, valuesList);
            }

            publishProgress(container);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(DataContainer... values) {
        Log.d("NewMapActivity", "Updating progress!");
        ((IOnMapDataDownloaded) context).onMapDataDownloaded(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("NewMapActivity", "Task done!");
    }

    public class DataContainer {
        public List<Model> dates = new ArrayList<Model>();
        public List<MapCluster> clusters = new ArrayList<MapCluster>();
    }

}
