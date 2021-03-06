package com.emreeran.sample;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.emreeran.locate.Locate;
import com.emreeran.locate.OnLocationChangedListener;
import com.emreeran.locate.Settings;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locate locate = Locate.getInstance();
        locate.enableDebug(true);
        locate.initialize(this,
                new Settings.Builder()
                        .interval(10000)
                        .smallestDisplacement(0)
                        .priority(Settings.Priority.HIGH)
                        .build());
        locate.requestLocationUpdates(this, new OnLocationChangedListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("MainActivity", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Locate.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
