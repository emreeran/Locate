package com.emreeran.locate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;

import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Emre Eran on 29/05/2017.
 */

/**
 * Possible codes:
 * ConnectionResult.SUCCESS
 * ConnectionResult.SERVICE_MISSING
 * ConnectionResult.SERVICE_UPDATING
 * ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
 * ConnectionResult.SERVICE_DISABLED
 * ConnectionResult.SERVICE_INVALID
 */
class GooglePlayServicesHelper {
    static int getGooglePlayServicesStatus(Context context) {
        if (context != null) {
            return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        } else {
            return -1;
        }
    }

    static boolean isGooglePlayServicesErrorUserResolvable(int errorCode) {
        return GoogleApiAvailability.getInstance().isUserResolvableError(errorCode);
    }

    static Dialog getGooglePlayServicesErrorResolveDialog(
            Activity activity, int errorCode, int requestCode, OnCancelListener onCancelListener) {
        if (activity == null) {
            return null;
        }

        return GoogleApiAvailability.getInstance().getErrorDialog(activity, errorCode, requestCode, onCancelListener);
    }
}
