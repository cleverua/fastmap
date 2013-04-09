package com.cleverua.fastmap;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.cleverua.fastmap.helpers.DBHelper;
import com.cleverua.fastmap.interfaces.IOnMapDataDownloaded;
import com.cleverua.fastmap.models.MapCluster;
import com.cleverua.fastmap.models.Model;
import com.cleverua.fastmap.tasks.GetMapDataTask;
import com.cleverua.fastmap.utils.MapClusterUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MapActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLongClickListener, IOnMapDataDownloaded {

    private GoogleMap mMap;
    private HashMap<Marker, Model> modelMarkersHash;
    private HashMap<Marker, MapCluster> clustersMarkersHash;
    private HashSet<String> lastQueries;

    private int lastMapZoom = 0;

    private GetMapDataTask getDatesTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        modelMarkersHash = new HashMap<Marker, Model>();
        clustersMarkersHash = new HashMap<Marker, MapCluster>();
        lastQueries = new HashSet<String>();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:

                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (checkForGoogleServices()){
            setUpMapIfNeeded();
        } else {
            Toast.makeText(this, "Please install Google Play Services...", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnInfoWindowClickListener(this);
                mMap.setOnMarkerClickListener(this);
                mMap.setOnCameraChangeListener(this);
                mMap.setOnMapLongClickListener(this);
                Log.d(TAG, "Map initiated!");
                Log.d(TAG, "Max zoom level = " + mMap.getMaxZoomLevel());
                Log.d(TAG, "Min zoom level = " + mMap.getMinZoomLevel());

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if ((int) cameraPosition.zoom != lastMapZoom){
            Log.d(TAG, "!!!!!Clearing map. Saved zoom = " + lastMapZoom + ", currentZoom = " + (int) cameraPosition.zoom);
            clustersMarkersHash.clear();
            modelMarkersHash.clear();
            lastMapZoom = (int) cameraPosition.zoom;
            mMap.clear();
            lastQueries.clear();
            DBHelper.clearTable(MapActivity.this, DBHelper.TABLE_MODEL);
        }

        LatLng sw = mMap.getProjection().getVisibleRegion().latLngBounds.southwest; // left bottom
        LatLng ne = mMap.getProjection().getVisibleRegion().latLngBounds.northeast; // right top
        LatLng nw = new LatLng(ne.latitude, sw.longitude); // left top
        LatLng se = new LatLng(sw.latitude, ne.longitude); // right bottom

        Log.d(TAG, "Lng diff = " + (se.longitude - nw.longitude) + "Lat diff = " + (nw.latitude - se.latitude));

        String[] qTreeIndex = new String[4];
        qTreeIndex[0] = MapClusterUtils.quadTree(sw.latitude, sw.longitude);
        qTreeIndex[1] = MapClusterUtils.quadTree(nw.latitude, nw.longitude);
        qTreeIndex[2] = MapClusterUtils.quadTree(ne.latitude, ne.longitude);
        qTreeIndex[3] = MapClusterUtils.quadTree(se.latitude, se.longitude);

        double lngDiff = se.longitude - nw.longitude;
        int length = MapClusterUtils.getQtreeLength(lngDiff) - 1; // -1 installed in experimental way
        Log.d(TAG, "qTreeLength = " + length);

        List<String> list = new ArrayList<String>();
        list.add(qTreeIndex[0].substring(0, length));
        list.add(qTreeIndex[1].substring(0, length));
        list.add(qTreeIndex[2].substring(0, length));
        list.add(qTreeIndex[3].substring(0, length));

        if (list.size() != 0 && !lastQueries.containsAll(list)){
            if (getDatesTask != null && getDatesTask.getStatus() != AsyncTask.Status.FINISHED){
                getDatesTask.cancel(true);
                getDatesTask = null;
            }
            lastQueries.addAll(list);
            getDatesTask = new GetMapDataTask(MapActivity.this, list);
            //getDatesTask.execute();
        }
    }

    private void showClustersOnMap(List<MapCluster> list){
        if (list != null && mMap != null){
            Log.d(TAG, "Drawing clusters on map...SIZE = " + list.size());
            for (MapCluster cluster: list){
                if (!clustersMarkersHash.containsValue(cluster)){
                    Log.d(TAG, "Cluster coords: " + cluster.getLat() + ", " + cluster.getLng());
                    MarkerOptions mOptions = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .position(new LatLng(cluster.getLat(), cluster.getLng()));
                    clustersMarkersHash.put(mMap.addMarker(mOptions), cluster);
                }
            }
        }
    }

    private void showModelsOnMap(List<Model> list){
        if (list != null && mMap != null){
            Log.d(TAG, "Drawing shared dates on map... SIZE = " + list.size());
            for (Model model: list){
                if (model.getLat() != null && model.getLng() != null) {
                    Log.d(TAG, "Date coords: " + model.getLat() + ", " + model.getLng());
                    MarkerOptions mOptions = new MarkerOptions()
                            .position(new LatLng(model.getLat(), model.getLng()))
                            .title(model.getTitle());
                    //Adding Marker to HashMap and mMap
                    modelMarkersHash.put(mMap.addMarker(mOptions), model);
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(MapActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
        marker.hideInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (clustersMarkersHash.containsKey(marker)){
            MapCluster cluster = clustersMarkersHash.get(marker);
            LatLngBounds bounds = new LatLngBounds(new LatLng(cluster.getMinLat(), cluster.getMinLng()), new LatLng(cluster.getMaxLat(), cluster.getMaxLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom));
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final Dialog d = new Dialog(MapActivity.this);
        d.setTitle("Add location");
        d.setContentView(R.layout.add_dialog_layout);
        final EditText etTitle = (EditText) findViewById(R.id.add_title);
        d.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO start task here!!!
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onMapDataDownloaded(GetMapDataTask.DataContainer result) {
        showModelsOnMap(result.dates);
        showClustersOnMap(result.clusters);
    }

    private boolean checkForGoogleServices(){
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    private static final String TAG = MapActivity.class.getSimpleName();
}
