package com.emreeran.locate;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Emre Eran on 30/05/2017.
 */

class FuseProvider extends Provider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * GoogleApiClient instance to use FusedLocationApi
     */
    private GoogleApiClient mGoogleApiClient;
    private Settings mSettings;
    private OnLocationChangedListener mOnLocationChangedListener;
    private Location mLastLocation;

    FuseProvider(Context context) {
        // Get GoogleApiClient instance.
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    void requestLocationUpdates(@NonNull Context context, @NonNull Settings settings, OnLocationChangedListener listener) {
        mOnLocationChangedListener = listener;
        mSettings = settings;
        connect();
    }

    @Override
    Location getLastLocation() {
        return mLastLocation;
    }

    @Override
    void startService(Context context, Settings settings, Class<? extends LocateService> serviceClass) {
        if (PermissionValidator.checkLocationPermissions(context)) {
            Logger.i("Starting as service");
            // Save settings
            LocatePreferences.saveSettings(context, settings);
            LocatePreferences.setProvider(context, LocatePreferences.PROVIDER_FUSE);
            // Start service
            context.startService(new Intent(context, serviceClass));
        } else {
            // Permissions are missing somehow, fail with permissions missing.
            // TODO: fail with requiring permissions
        }
    }

    @Override
    void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.i("Connected to Google Api Client");

        // Client is connected, continue with setting up location updates.
        startWithListener(mSettings);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: handle connection suspension
        Logger.i("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.i("Connection failed");
        // Connection failed, check if error is user resolvable. Falls back to LocationManager if not.
        if (mGoogleApiClient != null) {
            Locate.getInstance().resolveGooglePlayServicesConnectionIssue(
                    mGoogleApiClient.getContext(),
                    connectionResult.getErrorCode(),
                    mSettings.isShouldDisplayDialogIfGooglePlayErrorIsResolvable()
            );
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mOnLocationChangedListener != null) {
            mOnLocationChangedListener.onLocationChanged(location);
        }
    }

    private void connect() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                // Reconnect if already connected.
                mGoogleApiClient.reconnect();
            } else {
                // Connect to api if not connected.
                mGoogleApiClient.connect();
            }
        }
    }

    // Permissions are checked in PermissionValidator
    @SuppressWarnings("MissingPermission")
    private void startWithListener(Settings settings) {
        if (mGoogleApiClient != null) {
            Context context = mGoogleApiClient.getContext();

            if (PermissionValidator.checkLocationPermissions(context)) {
                LocationRequest locationRequest = settings.createGooglePlayLocationRequest();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            } else {
                // Permissions are missing somehow, fail with permissions missing.
                // TODO: fail with requiring permissions
            }
        } else {
            // Client is null for some reason, fallback to LocationManager
            // TODO: fallback to LocationManager
        }
    }
}
