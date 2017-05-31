package com.emreeran.locate;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

/**
 * Created by Emre Eran on 30/05/2017.
 */

class LocationManagerProvider {

    private LocationManager mLocationManager;

    LocationManagerProvider(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Settings settings = Locate.getInstance().getSettings();
        if (settings.isRunAsService()) {
            startAsService(context, settings);
        } else {
            startWithListener(context, settings);
        }
    }

    // TODO: implement GPS only, revise
    // Permissions are checked in PermissionValidator
    @SuppressWarnings("MissingPermission")
    private void startWithListener(Context context, Settings settings) {
        Logger.i("Starting LocationManagerProvider with listener");

        if (PermissionValidator.checkLocationPermissions(context)) {
            Criteria criteria = settings.createCriteria();
            float minDistance = settings.getSmallestDisplacement();
            long minTime = settings.getInterval();

            mLocationManager.requestLocationUpdates(minTime, minDistance, criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // TODO: Return location
                    Logger.i("Location updated; long:" + location.getLongitude() + " lat: " + location.getLatitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Logger.i("Status of provider " + provider + " changed");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Logger.i("Provider " + provider + " is enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Logger.i("Provider " + provider + " is disabled");
                }
            }, Looper.getMainLooper());
        } else {
            Logger.i("Permissions are missing.");
            // Permissions are missing somehow, fail with permissions missing.
            // TODO: fail with requiring permissions
        }
    }

    // Permissions are checked in PermissionValidator
    @SuppressWarnings("MissingPermission")
    private void startAsService(Context context, Settings settings) {
        Logger.i("Starting LocationManagerProvider as service");
        if (PermissionValidator.checkLocationPermissions(context)) {
            Logger.i("Starting as service");
            // Save settings
            LocatePreferences.saveSettings(context, settings);
            // Start service
            context.startService(new Intent(context, LocationManagerService.class));
        } else {
            Logger.i("Permissions are missing.");
            // Permissions are missing somehow, fail with permissions missing.
            // TODO: fail with requiring permissions
        }
    }
}
