package com.emreeran.sample;

import android.location.Location;
import android.util.Log;

import com.emreeran.locate.LocateService;

/**
 * Created by Emre Eran on 31/05/2017.
 */

public class SampleService extends LocateService {
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        Log.d("SampleService", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
    }
}
