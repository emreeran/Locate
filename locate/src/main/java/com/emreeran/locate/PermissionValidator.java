package com.emreeran.locate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Emre Eran on 30/05/2017.
 * <p>
 * Manages permission operations.
 * </p>
 */

class PermissionValidator {

    static final int REQUEST_LOCATION_PERMISSION = 10021;

    /**
     * Checks if current context has location permissions
     *
     * @param context Current context
     * @return boolean
     */
    static boolean checkLocationPermissions(@NonNull Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    static void requestLocationPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // TODO: Explanation is needed.

            Logger.d("startLocationUpdates: " + "Show an expanation to the user");
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed, request permission.
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    static void requestLocationPermission(Fragment fragment) {
        if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // TODO: Explanation is needed.
            Logger.d("startLocationUpdates: " + "Show an expanation to the user");
        } else {
            // No explanation needed, request permission.
            fragment.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

}
