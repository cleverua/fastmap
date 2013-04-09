package com.cleverua.fastmap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MapActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }

    private static final String TAG = MapActivity.class.getSimpleName();
}
