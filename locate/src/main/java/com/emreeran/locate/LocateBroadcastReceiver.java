package com.emreeran.locate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

/**
 * Created by Emre Eran on 27/05/2017.
 */

public class LocateBroadcastReceiver extends BroadcastReceiver {

    static final int REQUEST_LOCATION_UPDATE = 10022;
    static final String ACTION_LOCATION_UPDATED = "com.emreeran.locate.intent.UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_LOCATION_UPDATED:
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Location location = (Location) extras.get("com.google.android.location.LOCATION");
                    if (location != null) {
                        Logger.i("Location updated; long:" + location.getLongitude() + " lat: " + location.getLatitude());
                        // TODO: send location update
                    } else {
                        Logger.i("Returned location is null.");
                    }
                }
                break;
        }
    }
}
