package com.emreeran.locate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Emre Eran on 30/05/2017.
 */

public class Locate {

    private static final int REQUEST_GOOGLE_PLAY_SERVICES_RESOLVE = 10023;

    private static Locate mInstance;

    private Settings mSettings;

    private Provider mProvider;

    private boolean mShouldStart = false, mShouldStartService = false;

    private OnLocationChangedListener mOnLocationChangedListener;

    private Class<? extends LocateService> mServiceClass;

    private Locate() {
    }

    public static Locate getInstance() {
        if (mInstance == null) {
            mInstance = new Locate();
        }

        return mInstance;
    }

    public void enableDebug(boolean isEnabled) {
        Logger.setDebug(isEnabled);
    }

    public void initialize(Activity activity, Settings settings) {
        mSettings = settings;
        handlePermissions(activity, settings.isShouldAskPermissions());
    }

    public void initialize(Fragment fragment, Settings settings) {
        mSettings = settings;
        handlePermissions(fragment, settings.isShouldAskPermissions());
    }

    public void requestLocationUpdates(Context context) {
        if (mProvider != null) {
            mProvider.requestLocationUpdates(context, mSettings, null);
        } else {
            mShouldStart = true;
        }
    }

    public void requestLocationUpdates(Context context, OnLocationChangedListener onLocationChangedListener) {
        if (mProvider != null) {
            mProvider.requestLocationUpdates(context, mSettings, onLocationChangedListener);
        } else {
            mShouldStart = true;
            mOnLocationChangedListener = onLocationChangedListener;
        }
    }

    public void startService(Context context, Class<? extends LocateService> serviceClass) {
        if (mProvider != null) {
            mProvider.startService(context, mSettings, serviceClass);
        } else {
            mShouldStartService = true;
            mServiceClass = serviceClass;
        }
    }

    public void stopService(Context context, Class<? extends LocateService> serviceClass) {
        context.stopService(new Intent(context, serviceClass));
    }

    public void stopLocationUpdates() {
        if (mProvider != null) {
            mProvider.stopLocationUpdates();
        }

        mShouldStart = false;
    }

    /**
     * Continues execution after permission request is resolved.
     * Checks granted or denied permissions, continues if has permissions, fails with permissions denied otherwise
     *
     * @param activity     Current activity, needed instead of context in case error dialog displays
     * @param requestCode  Request Code returned from onRequestPermissionsResult
     * @param permissions  Permissions returned from onRequestPermissionsResult
     * @param grantResults Granted permissions result returned from onRequestPermissionsResult
     */
    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionValidator.REQUEST_LOCATION_PERMISSION) {
            boolean coarseLocationPermissionGranted = false;
            boolean fineLocationPermissionGranted = false;

            // Check permission results.
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResult == PackageManager.PERMISSION_GRANTED) {
                    coarseLocationPermissionGranted = true;
                } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResult == PackageManager.PERMISSION_GRANTED) {
                    fineLocationPermissionGranted = true;
                }
            }

            if (coarseLocationPermissionGranted && fineLocationPermissionGranted) {
                // Both permissions granted, continue.
                onPermissionsValidated(activity, mSettings.isShouldUseFuseProvider(),
                        mSettings.isShouldDisplayDialogIfGooglePlayErrorIsResolvable());
            } else if (coarseLocationPermissionGranted) {
                // Only coarse location permission granted, continue with gps denied.
                mSettings.setCanUseGps(false);
                onPermissionsValidated(activity, mSettings.isShouldUseFuseProvider(),
                        mSettings.isShouldDisplayDialogIfGooglePlayErrorIsResolvable());
            } else {
                // Both permissions denied, fail.
                // TODO: fail with permissions denied
            }
        }
    }

    public Location getLastLocation() {
        if (mProvider != null) {
            return mProvider.getLastLocation();
        }
        return null;
    }

    public OnLocationChangedListener getOnLocationChangedListener() {
        return mOnLocationChangedListener;
    }

    public void setOnLocationChangedListener(OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
    }
    // Package private methods

    Settings getSettings() {
        return mSettings;
    }

    void resolveGooglePlayServicesConnectionIssue(Context context, int errorCode, boolean shouldDisplayDialogIfErrorResolvable) {
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }

        if (errorCode == ConnectionResult.SERVICE_MISSING || errorCode == ConnectionResult.SERVICE_INVALID) {
            // Google Play Services are missing, fallback to LocationManager
            continueWithLocationManager(context);
        }
        // Google Play Services are not available, check if the problem is resolvable by user.
        else if (activity != null && shouldDisplayDialogIfErrorResolvable &&
                GooglePlayServicesHelper.isGooglePlayServicesErrorUserResolvable(errorCode)) {
            // Problem is resolvable by user and should display dialog.
            Logger.i("Resolvable issue with Google Play Services. Showing resolve dialog.");
            final Context dialogContext = context; // TODO: check if this causes problems
            Dialog resolveDialog = GooglePlayServicesHelper.getGooglePlayServicesErrorResolveDialog(
                    activity, errorCode, REQUEST_GOOGLE_PLAY_SERVICES_RESOLVE, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            // Google play error resolution cancelled by user, fallback to LocationManager.
                            continueWithLocationManager(dialogContext);
                        }
                    });
            resolveDialog.show();
        } else {
            // Problem is not resolvable by user, fallback to LocationManager.
            Logger.i("Google Play Services issue is not resolvable. Falling back to LocationManager.");
            continueWithLocationManager(context);
        }
    }

    // Private methods

    private void handlePermissions(Activity activity, boolean shouldRequestForPermissionsOnRuntime) {
        Logger.i("Checking permissions.");
        if (PermissionValidator.checkLocationPermissions(activity)) {
            // Context has location permissions.
            Logger.i("Permissions are granted");
            onPermissionsValidated(activity, mSettings.isShouldUseFuseProvider(),
                    mSettings.isShouldDisplayDialogIfGooglePlayErrorIsResolvable());
        } else {
            // Location permissions are missing.
            Logger.i("Permissions missing.");
            if (shouldRequestForPermissionsOnRuntime) {
                // Request permissions.
                PermissionValidator.requestLocationPermission(activity);
            } else {
                // Do not request permissions.
                // TODO: fail with should request permissions
            }
        }
    }

    private void handlePermissions(Fragment fragment, boolean shouldRequestForPermissionsOnRuntime) {
        Logger.i("Checking permissions.");
        if (PermissionValidator.checkLocationPermissions(fragment.getContext())) {
            // Context has location permissions.
            Logger.i("Permissions are granted");
            onPermissionsValidated(fragment.getContext(), mSettings.isShouldUseFuseProvider(),
                    mSettings.isShouldDisplayDialogIfGooglePlayErrorIsResolvable());
        } else {
            // Location permissions are missing.
            Logger.i("Permissions missing.");
            if (shouldRequestForPermissionsOnRuntime) {
                // Request permissions.
                PermissionValidator.requestLocationPermission(fragment);
            } else {
                // Do not request permissions.
                // TODO: fail with should request permissions
            }
        }
    }

    private void onPermissionsValidated(Context context, boolean shouldUseFuseProvider, boolean shouldDisplayDialogIfErrorResolvable) {
        if (shouldUseFuseProvider) {
            // Check if Google Play Services available, continue with fuse privider if available, fallback to LocationManager if not.
            checkGooglePlayServices(context, shouldDisplayDialogIfErrorResolvable);
        } else {
            // Fallback to LocationManager.
            continueWithLocationManager(context);
        }
    }

    private void checkGooglePlayServices(Context context, boolean shouldDisplayDialogIfErrorResolvable) {
        Logger.i("Checking if Google Play Services are available.");
        int googlePlayServicesStatus = GooglePlayServicesHelper.getGooglePlayServicesStatus(context);
        if (googlePlayServicesStatus == ConnectionResult.SUCCESS) {
            // Google Play Services are available, continue with fuse provider.
            Logger.i("Google Play Services are available");
            continueWithFuseProvider(context);
        } else {
            // Try to resolve issue if connection is not successful
            Logger.i("Google Play Services are not available");
            resolveGooglePlayServicesConnectionIssue(context, googlePlayServicesStatus, shouldDisplayDialogIfErrorResolvable);
        }
    }

    private void continueWithFuseProvider(Context context) {
        // Create FuseProvider
        Logger.i("Creating Fuse Provider");
        mProvider = new FuseProvider(context);

        if (mShouldStart) {
            mProvider.requestLocationUpdates(context, mSettings, mOnLocationChangedListener);
        } else if (mShouldStartService) {
            mProvider.startService(context, mSettings, mServiceClass);
        }
    }

    private void continueWithLocationManager(Context context) {
        // Create LocationManagerProvider
        Logger.i("Creating LocationManager Provider");
        mProvider = new LocationManagerProvider(context);

        if (mShouldStart) {
            mProvider.requestLocationUpdates(context, mSettings, mOnLocationChangedListener);
        } else if (mShouldStartService) {
            mProvider.startService(context, mSettings, mServiceClass);
        }
    }
}
